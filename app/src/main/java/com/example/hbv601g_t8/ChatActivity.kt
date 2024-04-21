package com.example.hbv601g_t8

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import io.agora.rtc2.RtcEngine

class ChatActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: MaterialButton
    private lateinit var messageInput: TextInputEditText
    private lateinit var chatMessages : List<Message>
    private lateinit var newMessage: Message
    private var chatId: Long = 0
    private var currentUserId: Long = -1
    private lateinit var callButton: MaterialButton
    private lateinit var agoraManager: AgoraManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        currentUserId = getCurrentUserId()

        val bundle = intent.extras
        if (bundle != null) {
            chatId = bundle.getLong("CHAT_ID")
        }

        agoraManager = AgoraManager(this)

        runBlocking {
            getMessagesFromDatabase()
        }

        recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatActivity)
            adapter = ChatAdapter(chatMessages, currentUserId)
        }

        recyclerView.scrollToPosition(chatMessages.size - 1)

        sendButton = findViewById(R.id.sendButton)
        messageInput = findViewById(R.id.messageInput)
        callButton = findViewById(R.id.join_call_button)

        sendButton.setOnClickListener {

            val messageText = messageInput.text.toString()

            if (messageText.isNotEmpty()) {
                newMessage = Message(conversationID = chatId, message = messageText, read = false, senderID = currentUserId, sentAt = ZonedDateTime.now().toString())
                runBlocking {
                    ConversationService().sendMessage(conversationId = chatId, messageText = messageText)
                    getMessagesFromDatabase()
                }

                recyclerView = findViewById<RecyclerView>(R.id.chatRecyclerView).apply {
                    layoutManager = LinearLayoutManager(this@ChatActivity)
                    adapter = ChatAdapter(chatMessages, currentUserId)
                }

                (recyclerView.adapter as? ChatAdapter)?.notifyItemInserted(chatMessages.size - 1)
                recyclerView.scrollToPosition(chatMessages.size - 1)  // Scroll to the new message
                messageInput.text?.clear()
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
        chatMessages = ConversationService().getMessages(conversationId = chatId)!!
    }


    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }

}