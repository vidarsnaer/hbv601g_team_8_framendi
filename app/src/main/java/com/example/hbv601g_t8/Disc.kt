package com.example.hbv601g_t8

import kotlinx.serialization.Serializable




@Serializable
data class Disc(
    val discId: Long? = null,
    val name: String,
    val description: String,
    val type: String,
    val condition: String,
    val colour: String,
    val price: Int,
    val userId: Long,
    val latitude: Double,
    val longitude: Double
    //,val images: List<Image> = listOf()
)

