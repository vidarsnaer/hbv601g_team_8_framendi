package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val conversationid: Int,
    val buyerid: Int,
    val conversation_ended: Boolean,
    val sellerid: Int,
    val conversation_title: String
)

@Serializable
data class newConversationCreation(
    val buyerid: Int,
    val conversation_ended: Boolean,
    val sellerid: Int,
    val conversation_title: String
)