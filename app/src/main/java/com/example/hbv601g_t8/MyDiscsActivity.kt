package com.example.hbv601g_t8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.runBlocking

class MyDiscsActivity : AppCompatActivity() {
    private lateinit var myDiscsList: List<Disc>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_discs)

        myDiscsList = emptyList()

        runBlocking {
            selectMyDiscsFromDatabase()
        }

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(myDiscsList)
    }

    private suspend fun selectMyDiscsFromDatabase () {
        myDiscsList = SupabaseManager.supabase.from("discs").select {
            filter {
                eq("user_id", 1)
            }
        }.decodeList<Disc>()
    }
}