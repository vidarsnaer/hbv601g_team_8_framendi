package com.example.hbv601g_t8

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class FavoriteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var allDiscsList: List<Disc>
    private lateinit var favoriteMark: List<Favorite>
    private lateinit var favoriteDiscs: List<Disc>
    private var currentUserId : Long = -1
    private lateinit var discImages: MutableMap<Int, Bitmap>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = prefs.getLong(GlobalVariables.USER_ID, -1)

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
                if(disc.discId != null) {
                    val intDiscId = disc.discId.toInt()
                    val imageUrl = SupabaseManager.supabase.storage.from("Images")
                        .publicUrl("${intDiscId}/image")
                    val bitmap = loadImageFromUrl(imageUrl)
                    bitmap?.let {
                        discImages[intDiscId] = it
                    }
                }
            }
        }

        runBlocking {
            favoriteDiscs = DiscService().getFavoriteDiscs()!!
        }

        println(favoriteDiscs)

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(favoriteDiscs, discImages)
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }
}
