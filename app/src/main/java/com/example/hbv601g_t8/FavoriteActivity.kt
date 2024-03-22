package com.example.hbv601g_t8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoriteActivity : AppCompatActivity() {

    private lateinit var newArrayList: ArrayList<Disc>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        //TODO: Get favorites from db

        newArrayList = arrayListOf(
            Disc(1, "used", "red disc slightly used", "Red Driver", 1000, "driver", 1, "red", 66.497650, -19.202146),
            )

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(newArrayList)
    }
}
