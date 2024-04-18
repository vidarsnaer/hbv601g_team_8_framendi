package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Favorite(
    val id: Long? = null,
    val userId: Long,
    val discId: Long
)
