package com.example.hbv601g_t8

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var allDiscsList: List<Disc>
    private lateinit var favoriteMark: List<FavoriteMark>
    private lateinit var favoriteDiscs: List<Disc>

    @Serializable
    data class FavoriteMark(
        val id : Int,
        val disc_discid : Int,
        val user_id : Int
    )

    @Serializable
    data class AddFavoriteMark(
        val disc_discid : Int,
        val user_id : Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        //TODO: Get favorites from db

        runBlocking {
            withContext(Dispatchers.IO) {
                allDiscsList = SupabaseManager.supabase.from("discs").select().decodeList()
                favoriteMark = SupabaseManager.supabase.from("favorite").select {
                    filter {
                        eq("user_id", 1)
                    }
                }.decodeList()
            }
        }

        favoriteDiscs = allDiscsList.filter {
            disc -> favoriteMark.any { mark -> mark.disc_discid == disc.discid }
        }

        println(favoriteDiscs)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(favoriteDiscs)
    }
}
