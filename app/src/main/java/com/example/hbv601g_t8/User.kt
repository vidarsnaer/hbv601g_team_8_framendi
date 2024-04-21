package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class User(
    var id: Long? = null,  // Initialize as null since it will be set by the backend
    val name: String,
    val email: String,
    val password: String
)

/*
data class User(
    val id: String,
    var name: String,
    val email: String,
    val password: String
)
*/