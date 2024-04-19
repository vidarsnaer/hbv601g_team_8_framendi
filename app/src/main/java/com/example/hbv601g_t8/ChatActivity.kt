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
import io.agora.rtc2.RtcEngine

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: MaterialButton
    private lateinit var messageInput: TextInputEditText
    private lateinit var chatMessages : List<Message>
    private lateinit var newMessage: NewMessage
    private lateinit var callButton: MaterialButton
    private lateinit var agoraManager: AgoraManager

    private var chatId : Int = 0
    private val currentUserId = GlobalVariables.USER_ID!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val bundle = intent.extras
        if (bundle != null) {
            chatId = bundle.getInt("CHAT_ID")
        }

        println(chatId)

        agoraManager = AgoraManager(this)

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
        callButton = findViewById(R.id.join_call_button)

        sendButton.setOnClickListener {

            val messageText = messageInput.text.toString()

            if (messageText.isNotEmpty()) {
                newMessage = NewMessage(chatId, messageText, false, currentUserId, ZonedDateTime.now().toString())

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

        callButton.setOnClickListener {

            if (callButton.text.toString() == "Join Call") {
                callButton.setText(R.string.leave_call)
                callButton.setTextColor(getColor(R.color.danger))
                agoraManager.joinChannel("testing", "007eJxTYPDVPG+9e80Kg6lHD/Z07jZ+l7/nfHnl8lCtQJ4jFupZc64oMJgampkaWxilpRgmJpsYJhommpoaWqalGKclWRoYJZqknAiVTWsIZGSQCvvKyMgAgSA+O0NJanFJZl46AwMA0+cg2w==")
            } else {
                callButton.setText(R.string.join_call)
                callButton.setTextColor(getColor(R.color.blue))
                agoraManager.leaveChannel()
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