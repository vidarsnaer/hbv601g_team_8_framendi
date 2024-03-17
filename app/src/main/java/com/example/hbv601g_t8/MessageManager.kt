package com.example.hbv601g_t8

import java.time.ZonedDateTime

object MessageManager {

    private val dummyMessages = mutableListOf<Message>(
        Message(1, "halló", 1, 2, ZonedDateTime.now(), true),
        Message(2, "hæ", 1, 1, ZonedDateTime.now(), true),
        Message(3, ":)", 1, 2, ZonedDateTime.now(), false),
        Message(4, "Má ég plís fá fribsídiskinn", 2, 3, ZonedDateTime.now(), true),
        Message(5, "100kr?", 3, 1, ZonedDateTime.now(), true),
        Message(6, "nei", 3, 4, ZonedDateTime.now(), false)
        // Add your initial messages here
    )

    fun getMessagesForConversation(conversationId: Long): MutableList<Message> {
        return if (conversationId == (-1).toLong()) {
            mutableListOf<Message>()
        } else {
            dummyMessages.filter { it.conversationId == conversationId } as MutableList<Message>

        }
    }

    fun addMessage(message: Message) {
        dummyMessages.add(message)
    }

    // You can add other methods as needed, for example, to delete a message, update a message, etc.
}
