package com.example.hbv601g_t8

object ConversationManager {

    private val conversation1 = Conversation(1, 2, 1, false, "Liverpool")
    private val conversation2 = Conversation(2, 1, 2, false, "Chat 2")
    private val conversation3 = Conversation(3, 1, 3, false, "ch3")

    private val dummyConvos = mutableListOf<Conversation>(conversation1, conversation2, conversation3)

    fun getConversations(): MutableList<Conversation> {
        return dummyConvos
    }

    fun addConversation(conversation: Conversation) {
        dummyConvos.add(conversation)
    }

    // Optionally, add more functionality as needed, such as removing a conversation, finding a conversation by ID, etc.
}