package com.example.hbv601g_t8

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class ViewDiscActivity: AppCompatActivity() {

    private  var discid : Int = 0
    private lateinit var title : TextView
    private lateinit var price : TextView
    private lateinit var condition : TextView
    private lateinit var type : TextView
    private lateinit var color : TextView
    private lateinit var description : TextView
    private lateinit var newArrayList: ArrayList<Disc>
    private lateinit var nextImage : Button
    private lateinit var prevImage : Button
    private lateinit var image : ImageView
    private lateinit var messageOwner : Button
    private lateinit var favorites : Button

    private var inFavorites = -1



    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.disc_overview)

        val bundle = intent.extras
        if (bundle != null) {
            discid = bundle.getInt("discid")
        }

        newArrayList = arrayListOf(
            Disc(1, "used", "red disc slightly used", "Red Driver", 1000, "driver", 1, "red"),
            Disc(2, "used", "pink disc which is new", "Pink Driver", 1000, "driver", 1, "pink"),
            Disc(3, "used", "driver disc, not used", "Driver", 1000, "driver", 1, "black")
        )

        val disc = newArrayList.find { it.discid == discid }

        title = findViewById(R.id.title)
        title.text = disc?.name
        price = findViewById(R.id.price)
        price.text = disc?.price.toString()
        condition = findViewById(R.id.condition)
        condition.text = disc?.condition
        type = findViewById(R.id.type)
        type.text = disc?.type
        color = findViewById(R.id.color)
        color.text = disc?.colour
        description = findViewById(R.id.description)
        description.text = disc?.description
        image = findViewById(R.id.image)
        image.setImageResource(R.drawable.frisbee);

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


    }
}