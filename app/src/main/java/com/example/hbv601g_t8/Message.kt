package com.example.hbv601g_t8

import java.time.ZonedDateTime

data class Message(
    val messageId: Long,
    val message: String,
    val conversationId: Long,
    val senderId: Long,
    val sentAt: ZonedDateTime,
    val read: Boolean
)