package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val conversationid: Int,
    val buyerid: String,
    val conversation_ended: Boolean,
    val sellerid: String,
    val conversation_title: String
)

@Serializable
data class newConversationCreation(
    val buyerid: String,
    val conversation_ended: Boolean,
    val sellerid: String,
    val conversation_title: String
)