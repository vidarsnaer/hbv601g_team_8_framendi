package com.example.hbv601g_t8

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Conversation(
    val conversationid: Int,
    @Serializable(with = UUIDSerializer::class)
    val buyerid: UUID,
    val conversation_ended: Boolean,
    @Serializable(with = UUIDSerializer::class)
    val sellerid: UUID,
    val conversation_title: String
)

@Serializable
data class newConversationCreation(
    @Serializable(with = UUIDSerializer::class)
    val buyerid: UUID,
    val conversation_ended: Boolean,
    @Serializable(with = UUIDSerializer::class)
    val sellerid: UUID,
    val conversation_title: String
)