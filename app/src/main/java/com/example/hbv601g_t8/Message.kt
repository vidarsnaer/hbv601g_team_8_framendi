package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val conversationid: Int,
    val message: String,
    val read: Boolean,
    val senderid: Int,
    val sent_at: String
)