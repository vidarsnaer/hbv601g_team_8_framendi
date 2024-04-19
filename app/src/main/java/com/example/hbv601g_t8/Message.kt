package com.example.hbv601g_t8

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Message(
    val message_id: Int,
    val conversationid: Int,
    val message: String,
    val read: Boolean,
    @Serializable(with = UUIDSerializer::class)
    val senderid: UUID,
    val sent_at: String
)

@Serializable
data class NewMessage(
    val conversationid: Int,
    val message: String,
    val read: Boolean,
    @Serializable(with = UUIDSerializer::class)
    val senderid: UUID,
    val sent_at: String
)