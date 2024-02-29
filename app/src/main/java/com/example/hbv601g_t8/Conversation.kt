package com.example.hbv601g_t8

data class Conversation(
    val conversationId: Long,
    val buyerID: Long,
    val sellerID: Long,
    val conversationEnded: Boolean,
    val conversationTitle: String
)