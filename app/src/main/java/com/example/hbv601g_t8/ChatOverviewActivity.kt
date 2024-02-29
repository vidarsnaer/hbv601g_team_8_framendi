package com.example.hbv601g_t8

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.ZonedDateTime

class ChatOverviewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private var conversation1 = Conversation(1, 2, 1, false, "Liverpool")
    private var conversation2 = Conversation(2, 1, 2, false, "Chat 2")
    private var conversation3 = Conversation(3, 1, 3, false, "ch3")

    private var dummyConvos = arrayListOf(conversation1, conversation2, conversation3)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)

        recyclerView = findViewById<RecyclerView>(R.id.chatOverviewRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatOverviewActivity)
            adapter = ChatOverviewAdapter(dummyConvos) { chatId ->
                val intent = Intent(this@ChatOverviewActivity, ChatActivity::class.java).apply {
                    putExtra("CHAT_ID", chatId)
                }
                startActivity(intent)
            }
        }
    }
}
