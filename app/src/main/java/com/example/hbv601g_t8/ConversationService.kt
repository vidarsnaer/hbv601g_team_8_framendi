package com.example.hbv601g_t8

import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConversationService {

    suspend fun insertNewMessageToDatabase (newMessage: Message) {
        withContext(Dispatchers.IO) {
            SupabaseManager.supabase.from("message").insert(newMessage)
        }
    }

    suspend fun getMessagesFromDatabase (chatId: Int): List<Message> {
        var chatMessages: List<Message>
        withContext(Dispatchers.IO) {
            chatMessages = SupabaseManager.supabase.from("message").select {
                filter {
                    eq("conversationid", chatId)
                }
            }.decodeList()
        }
        return chatMessages
    }

    suspend fun getConversationsFromDatabase(): List<Conversation> {
        var conversations: List<Conversation>
        withContext(Dispatchers.IO) {
            conversations = SupabaseManager.supabase.from("conversation").select().decodeList()
        }
        return conversations
    }

    suspend fun insertNewConversationToDatabase(newConversation: newConversationCreation): Conversation {
        var result: Conversation
        withContext(Dispatchers.IO) {
            result = SupabaseManager.supabase.from("conversation").insert(newConversation) {
                select()
            }.decodeSingle()
        }
        return result
    }
}