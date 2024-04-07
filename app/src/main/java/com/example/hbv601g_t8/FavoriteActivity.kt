package com.example.hbv601g_t8

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var allDiscsList: List<Disc>
    private lateinit var favoriteMark: List<FavoriteMark>
    private lateinit var favoriteDiscs: List<Disc>
    private lateinit var currentUserId : String
    private lateinit var discImages: MutableMap<Int, Bitmap>

    @Serializable
    data class FavoriteMark(
        val id : Int,
        val disc_discid : Int,
        val user_id : String
    )

    @Serializable
    data class AddFavoriteMark(
        val disc_discid : Int,
        val user_id : String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getString(GlobalVariables.USER_ID, "No id found").toString()

        discImages = mutableMapOf<Int, Bitmap>()

        suspend fun loadImageFromUrl(imageUrl: String): Bitmap? = withContext(Dispatchers.IO) {
            return@withContext try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input = connection.inputStream
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }

        suspend fun getImages(){
            for (disc in allDiscsList) {
                val imageUrl = SupabaseManager.supabase.storage.from("Images").publicUrl("${disc.discid}/image")
                val bitmap = loadImageFromUrl(imageUrl)
                bitmap?.let {
                    discImages[disc.discid] = it
                }
            }
        }

        runBlocking {
            withContext(Dispatchers.IO) {
                allDiscsList = SupabaseManager.supabase.from("discs").select().decodeList()
                favoriteMark = SupabaseManager.supabase.from("favorite").select {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }.decodeList()
            }
            getImages()
        }

        favoriteDiscs = allDiscsList.filter {
            disc -> favoriteMark.any { mark -> mark.disc_discid == disc.discid }
        }

        println(favoriteDiscs)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(favoriteDiscs, discImages)
    }
}
