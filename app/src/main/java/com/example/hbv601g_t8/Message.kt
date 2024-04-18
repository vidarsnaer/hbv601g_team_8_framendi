package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val messageID: Long? = null,
    val conversationID: Long,
    val senderID: Long,
    val message: String,
    val sentAt: String,  // TODO: Make sure the date format matches between frontend and backend
    val read: Boolean
)
