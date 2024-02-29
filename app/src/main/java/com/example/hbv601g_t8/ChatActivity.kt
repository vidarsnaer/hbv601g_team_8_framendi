package com.example.hbv601g_t8

import ChatAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.button.MaterialButton
import java.time.ZonedDateTime
import com.example.hbv601g_t8.Message

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: MaterialButton
    private lateinit var messageInput: TextInputEditText

    private val currentUserId = 1.toLong()

    private var counter = 4.toLong()

    private var message1 = Message(1, "Hello", 1, 2, ZonedDateTime.now(), true)
    private var message2 = Message(2, "Hi", 1, currentUserId, ZonedDateTime.now(), true)
    private var message3 = Message(3, ":)", 1, 2, ZonedDateTime.now(), false)

    private var dummyMessages = arrayListOf(message1, message2, message3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val chatId = intent.getLongExtra("CHAT_ID", -1) // Default value as -1
        if (chatId == (-1).toLong()) {
            println("skoða seinna")
        }

        recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = ChatAdapter(dummyMessages, currentUserId)
        }

        sendButton = findViewById(R.id.sendButton)
        messageInput = findViewById(R.id.messageInput)

        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString()

            if (messageText.isNotEmpty()) {
                val newMessage = Message(counter++, messageText, 1, currentUserId, ZonedDateTime.now(), true)
                dummyMessages.add(newMessage)
                (recyclerView.adapter as? ChatAdapter)?.notifyItemInserted(dummyMessages.size - 1)
                recyclerView.scrollToPosition(dummyMessages.size - 1)  // Scroll to the new message
                messageInput.text?.clear()
                // TODO: Implement the logic to send the message to the backend and update the UI accordingly
            }

        }
    }


}
