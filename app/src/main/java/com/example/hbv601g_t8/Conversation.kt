package com.example.hbv601g_t8

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val conversationID: Long? = null,
    val buyerID: Long,
    val sellerID: Long,
    val conversationEnded: Boolean,
    val conversationTitle: String