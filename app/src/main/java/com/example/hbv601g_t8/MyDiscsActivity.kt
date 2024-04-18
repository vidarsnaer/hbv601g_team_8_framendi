package com.example.hbv601g_t8

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MyDiscsActivity : AppCompatActivity() {
    private lateinit var myDiscsList: List<Disc>
    private lateinit var recyclerView: RecyclerView
    private var currentUserId : Long = -1
    private lateinit var discImages: MutableMap<Int, Bitmap>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.favorites)

        val myDiscsText = findViewById<Toolbar>(R.id.toolbar)
        myDiscsText.title = getString(R.string.my_discs)

        myDiscsList = emptyList()

        currentUserId = getCurrentUserId()

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
            for (disc in myDiscsList) {
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
            selectMyDiscsFromDatabase()
            getImages()
        }

        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DiscAdapter(myDiscsList, discImages)
    }

    private suspend fun selectMyDiscsFromDatabase () {
        DiscService().getMyDiscs(currentUserId)
        /*
        withContext(Dispatchers.IO) {
            myDiscsList = SupabaseManager.supabase.from("discs").select {
                filter {
                    eq("user_id", currentUserId)
                }
            }.decodeList<Disc>()
        }
        */
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }
}