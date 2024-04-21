package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hbv601g_t8.SupabaseManager.supabase
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class ViewDiscActivity: AppCompatActivity() {

    private var discid : Long = -1
    private var discOwnerId: Long = -1
    private lateinit var title : TextView
    private lateinit var price : TextView
    private lateinit var condition : TextView
    private lateinit var type : TextView
    private lateinit var color : TextView
    private lateinit var description : TextView
    private lateinit var nextImage : Button
    private lateinit var prevImage : Button
    private lateinit var image : ImageView
    private lateinit var messageOwner : Button
    private lateinit var favorites : Button
    private lateinit var discInfo : Disc
    private lateinit var editDiscInfo: Button

    private var currentUserId : Long = -1
    private lateinit var imageUrl : String
    private lateinit var imageBitmap : Bitmap



    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.disc_overview)

        val bundle = intent.extras
        if (bundle != null) {
            discid = bundle.getLong("discId")
            discOwnerId = bundle.getLong("discOwnerId")
        }

        currentUserId = getCurrentUserId()

        suspend fun selectDiscInfoFromDatabase() {
            discInfo = DiscService().getDisc(discid)!!
            /*
            withContext(Dispatchers.IO) {
                discInfo = SupabaseManager.supabase.from("discs").select {
                    filter {
                        eq("discid", discid)
                    }
                }.decodeSingle()
            }
            */
        }

        suspend fun loadImageFromUrl(imageUrl: String): Bitmap? {
            return try {
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

        image = findViewById(R.id.image)

        runBlocking {
            selectDiscInfoFromDatabase()
            val intDiscId = discid.toInt()
            imageUrl = supabase.storage.from("Images").publicUrl("${intDiscId}/image")
            GlobalScope.launch(Dispatchers.IO) {
                val bitmap = loadImageFromUrl(imageUrl)
                bitmap?.let {
                    withContext(Dispatchers.Main) {
                        image.setImageBitmap(it)
                    }
                }
            }
        }


        title = findViewById(R.id.title)
        title.text = discInfo.name
        price = findViewById(R.id.price)
        price.text = discInfo.price.toString()
        condition = findViewById(R.id.condition)
        condition.text = discInfo.condition
        type = findViewById(R.id.type)
        type.text = discInfo.type
        color = findViewById(R.id.color)
        color.text = discInfo.colour
        description = findViewById(R.id.description)
        description.text = discInfo.description



        /*
        nextImage = findViewById(R.id.next_image)
        prevImage = findViewById(R.id.previous_image)

        nextImage.setOnClickListener{
            Toast.makeText(this, "Next image", Toast.LENGTH_SHORT).show()
        }

        prevImage.setOnClickListener{
            Toast.makeText(this, "Previous image", Toast.LENGTH_SHORT).show()
        }*/


        messageOwner = findViewById(R.id.message_owner)
        messageOwner.setOnClickListener {
            val result : Conversation

            runBlocking {
                result = ConversationService().createConversation(sellerId = discOwnerId, title = discInfo.name)!!
                /*
                withContext(Dispatchers.IO) {
                    result = SupabaseManager.supabase.from("conversation").insert(newConversation) {
                        select()
                    }.decodeSingle()
                }
                */
            }

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("CHAT_ID", result.conversationID)
            }
            startActivity(intent)
            //Toast.makeText(this, "Message owner", Toast.LENGTH_SHORT).show()
        }

        var favorited : Boolean
        runBlocking { favorited = DiscService().isDiscFavorite(discid)!! }

        favorites = findViewById(R.id.favorite)

        if (favorited) {
            favorites.setBackgroundResource(R.drawable.baseline_favorite_24)
        }

        favorites.setOnClickListener{
            if(!favorited){
                runBlocking {
                    DiscService().addToFavorites(discid)
                }
                favorites.setBackgroundResource(R.drawable.baseline_favorite_24)
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                favorited = true
            } else {
                favorites.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(this, "Removed favorites", Toast.LENGTH_SHORT).show()
                runBlocking {
                    DiscService().removeFromFavorites(discid)
                }
                favorited = false
            }
        }

        editDiscInfo = findViewById(R.id.edit_disc_info)
        editDiscInfo.setOnClickListener {
            Toast.makeText(this, "Edit Disc Info", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, EditDiscActivity::class.java)
            intent.putExtra("discId", discid)
            startActivity(intent)
        }

        if (discInfo.userId == currentUserId) {
            editDiscInfo.visibility = View.VISIBLE
        }

    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }

}