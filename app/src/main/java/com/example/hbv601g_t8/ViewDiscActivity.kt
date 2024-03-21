package com.example.hbv601g_t8

import android.content.Intent
import android.graphics.drawable.Drawable
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

class ViewDiscActivity: AppCompatActivity() {

    private  var discid : Int = 0
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

    private var inFavorites = -1

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.disc_overview)

        val bundle = intent.extras
        if (bundle != null) {
            discid = bundle.getInt("discid")
        }

        suspend fun selectDiscInfoFromDatabase() {
            withContext(Dispatchers.IO) {
                discInfo = SupabaseManager.supabase.from("discs").select {
                    filter {
                        eq("discid", discid)
                    }
                }.decodeSingle()
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
        messageOwner.setOnClickListener{

            //TODO: Message owner function
            Toast.makeText(this, "Message owner", Toast.LENGTH_SHORT).show()
        }

        favorites = findViewById(R.id.favorite)
        favorites.setOnClickListener{
            if(inFavorites == -1){
                favorites.setBackgroundResource(R.drawable.baseline_favorite_24)
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()
                //TODO: add to favorites
            } else {
                favorites.setBackgroundResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(this, "Removed favorites", Toast.LENGTH_SHORT).show()
                //TODO: remove from favorites
            }
            inFavorites *= -1
        }

        editDiscInfo = findViewById(R.id.edit_disc_info)
        editDiscInfo.setOnClickListener {
            Toast.makeText(this, "Edit Disc Info", Toast.LENGTH_SHORT).show()
        }

        if (discInfo.user_id == 1) {
            editDiscInfo.visibility = View.VISIBLE
        }

    }
}