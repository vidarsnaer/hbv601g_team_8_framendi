package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class ChatOverviewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var conversations : List<Conversation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_overview)

        runBlocking {
            getConversationsFromDatabase()
        }

        recyclerView = findViewById<RecyclerView>(R.id.chatOverviewRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@ChatOverviewActivity)
            adapter = ChatOverviewAdapter(conversations) {conversationid ->
                val intent = Intent(this@ChatOverviewActivity, ChatActivity::class.java).apply {
                    putExtra("CHAT_ID", conversationid)
                }
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the conversations list every time the activity resumes
        recyclerView.adapter = ChatOverviewAdapter(conversations) { conversationid ->
            val intent = Intent(this@ChatOverviewActivity, ChatActivity::class.java).apply {
                putExtra("CHAT_ID", conversationid)
            }
            startActivity(intent)
        }
    }

    private suspend fun getConversationsFromDatabase() {
        withContext(Dispatchers.IO) {
            conversations = SupabaseManager.supabase.from("conversation").select().decodeList()
        }
    }

    suspend fun getConversation(conversationId: Long): Conversation? {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.networkingApi.getConversation(conversationId).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }

}
