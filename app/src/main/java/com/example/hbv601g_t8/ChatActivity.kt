package com.example.hbv601g_t8

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import java.time.ZonedDateTime
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: MaterialButton
    private lateinit var messageInput: TextInputEditText
    private lateinit var chatMessages : List<Message>
    private lateinit var newMessage: Message
    private var chatId : Int = 0

    private val currentUserId = 9

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val bundle = intent.extras
        if (bundle != null) {
            chatId = bundle.getInt("CHAT_ID")
        }

        println(chatId)

        runBlocking {
            getMessagesFromDatabase()
        }

        recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            //adapter = com.example.hbv601g_t8.ChatAdapter(chatMessages, currentUserId)
            adapter = ChatAdapter(chatMessages, currentUserId)
        }

        recyclerView.scrollToPosition(chatMessages.size - 1)

        sendButton = findViewById(R.id.sendButton)
        messageInput = findViewById(R.id.messageInput)

        sendButton.setOnClickListener {

            val messageText = messageInput.text.toString()

            if (messageText.isNotEmpty()) {
                newMessage = Message(chatId, messageText, false, currentUserId, ZonedDateTime.now().toString())

                runBlocking {
                    sendMessageToDatabase()
                    getMessagesFromDatabase()
                }

                recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
                    layoutManager = LinearLayoutManager(this@ChatActivity)
                    adapter = ChatAdapter(chatMessages, currentUserId)
                }

                (recyclerView.adapter as? ChatAdapter)?.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)  // Scroll to the new message
                messageInput.text?.clear()
                // TODO: Implement the logic to send the message to the backend and update the UI accordingly
            } else {
                Toast.makeText(this, "Message Must Not Be Empty", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private suspend fun getMessagesFromDatabase () {
        withContext(Dispatchers.IO) {
            chatMessages = SupabaseManager.supabase.from("message").select {
                filter {
                    eq("conversationid", chatId)
                }
            }.decodeList()
        }
    }

    private suspend fun sendMessageToDatabase () {
        withContext(Dispatchers.IO) {
            SupabaseManager.supabase.from("message").insert(newMessage)
        }
    }

}