package com.example.hbv601g_t8

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MyDiscsActivity : AppCompatActivity() {
    private lateinit var myDiscsList: List<Disc>
    private lateinit var recyclerView: RecyclerView
    private lateinit var currentUserId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        val myDiscsText = findViewById<Toolbar>(R.id.toolbar)
        myDiscsText.title = getString(R.string.my_discs)

        myDiscsList = emptyList()

        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getString(GlobalVariables.USER_ID, "No id found").toString()

        runBlocking {
            selectMyDiscsFromDatabase()
        }

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(myDiscsList)
    }

    private suspend fun selectMyDiscsFromDatabase () {
        withContext(Dispatchers.IO) {
            myDiscsList = SupabaseManager.supabase.from("discs").select {
                filter {
                    eq("user_id", currentUserId)
                }
            }.decodeList<Disc>()
        }
    }
}