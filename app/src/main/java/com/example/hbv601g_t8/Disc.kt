package com.example.hbv601g_t8

import android.graphics.Bitmap
import kotlinx.serialization.Serializable

@Serializable
data class Disc(
    val discid: Int,
    val condition: String,
    val description: String,
    val name: String,
    val price: Int,
    val type: String,
    val user_id: String,
    val colour: String,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class NewDiscCreation (
    val price: Int,
    val condition: String,
    val description: String,
    val name: String,
    val type: String,
    val colour: String,
    val user_id: String,
    val quantity: Int
)
