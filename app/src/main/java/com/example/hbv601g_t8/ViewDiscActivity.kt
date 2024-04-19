package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.ktor.util.Identity.decode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.properties.Delegates

class ViewDiscActivity: AppCompatActivity() {

    private var discid by Delegates.notNull<Int>()
    private lateinit var discOwnerId: String
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
    private lateinit var favouriteMark: List<FavoriteActivity.FavoriteMark>


    //TODO: nota rétt userId
    private lateinit var currentUserId : UUID
    private var ownerId : Long = (-1).toLong()
    private var conversationTitle : String = ""


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.disc_overview)

        val bundle = intent.extras
        if (bundle != null) {
            discid = bundle.getInt("discid")
            discOwnerId = bundle.getString("discOwnerId").toString()
        }

        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = GlobalVariables.USER_ID

        suspend fun selectDiscInfoFromDatabase() {
            withContext(Dispatchers.IO) {
                discInfo = SupabaseManager.supabase.from("discs").select {
                    filter {
                        eq("discid", discid)
                    }
                }.decodeSingle()
                favouriteMark = SupabaseManager.supabase.from("favorite").select {
                    filter {
                        eq("user_id", currentUserId)
                    }
                }.decodeList()
            }
        }

        runBlocking {
            selectDiscInfoFromDatabase()
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
        image = findViewById(R.id.image)
        image.setImageResource(R.drawable.frisbee)

        nextImage = findViewById(R.id.next_image)
        prevImage = findViewById(R.id.previous_image)

        nextImage.setOnClickListener{
            Toast.makeText(this, "Next image", Toast.LENGTH_SHORT).show()
        }

        prevImage.setOnClickListener{
            Toast.makeText(this, "Previous image", Toast.LENGTH_SHORT).show()
        }

        messageOwner = findViewById(R.id.message_owner)
        messageOwner.setOnClickListener {

            val newConversation = newConversationCreation (
                currentUserId.toString(),
                false,
                discOwnerId,
                "New Conversation Test"
            )
            val result : Conversation

            runBlocking {
                withContext(Dispatchers.IO) {
                    result = SupabaseManager.supabase.from("conversation").insert(newConversation) {
                        select()
                    }.decodeSingle()
                }
            }

            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("CHAT_ID", result.conversationid)
            }
            startActivity(intent)
            //Toast.makeText(this, "Message owner", Toast.LENGTH_SHORT).show()
        }

        var favorited = favouriteMark.any {mark -> mark.disc_discid == discid }

        favorites = findViewById(R.id.favorite)

        if (favorited) {
            favorites.setBackgroundResource(R.drawable.baseline_favorite_24)
        }

        favorites.setOnClickListener{
            if(!favorited){
                val addToFavourite = FavoriteActivity.AddFavoriteMark(discid, currentUserId.toString())
                favorites.setBackgroundResource(R.drawable.baseline_favorite_24)
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                runBlocking {
                    withContext(Dispatchers.IO) {
                        SupabaseManager.supabase.from("favorite").insert(addToFavourite)
                    }
                }
                favorited = true
            } else {
                favorites.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(this, "Removed favorites", Toast.LENGTH_SHORT).show()
                runBlocking {
                    withContext(Dispatchers.IO) {
                        SupabaseManager.supabase.from("favorite").delete {
                            filter {
                                eq("disc_discid", discid)
                                eq("user_id", currentUserId)
                            }
                        }
                    }
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

        if (discInfo.user_id == GlobalVariables.USER_ID) {
            editDiscInfo.visibility = View.VISIBLE
        }

    }
}