package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID


class DiscAdapter(private var discList: List<Disc>, private var discImages: Map<Int, Bitmap>):
    RecyclerView.Adapter<DiscAdapter.DiscViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        context = parent.context
        return DiscViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return discList.size
    }

    override fun onBindViewHolder(holder: DiscViewHolder, position: Int) {
        val currentItem = discList[position]
        val currentImage = discImages[currentItem.discid] // Get the corresponding image for the current disc id
        holder.title.text = currentItem.name
        holder.price.text = context.getString(R.string.kr, currentItem.price.toString())

        // Set the image if available

        currentImage?.let {
            holder.image.setImageBitmap(it)
        }

        holder.itemView.setOnClickListener {
            val discid: Int = discList[position].discid
            val discOwnerId: UUID = discList[position].user_id
            val intent = Intent(context, ViewDiscActivity::class.java)
            intent.putExtra("discid", discid)
            intent.putExtra("discOwnerId", discOwnerId.toString())
            context.startActivity(intent)
        }
    }

    class DiscViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.iv_image)
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val price: TextView = itemView.findViewById(R.id.tv_price)
    }

    fun updateData(newDiscs: List<Disc>, newImages: Map<Int, Bitmap>) {
        discList = newDiscs  // Update the disc list
        discImages = newImages  // Update the disc images map
        notifyDataSetChanged()  // Notify the adapter to refresh the RecyclerView
    }
}
