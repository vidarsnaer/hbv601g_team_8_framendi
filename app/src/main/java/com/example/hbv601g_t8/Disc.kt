package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Disc(
    val discid: Int,
    val condition: String,
    val description: String,
    val name: String,
    val price: Int,
    val type: String,
    val user_id: Int,
    val colour: String
)

@Serializable
data class NewDiscCreation (
    val price: Int,
    val condition: String,
    val description: String,
    val name: String,
    val type: String,
    val colour: String,
    val user_id: Int
)
