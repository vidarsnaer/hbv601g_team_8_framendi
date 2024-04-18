package com.example.hbv601g_t8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ConversationService {

    /*
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
    */


    suspend fun getAllConversations(): List<Conversation>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getAllConversations().execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getConversation(conversationId: Long): Conversation? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getConversation(conversationId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun endConversation(conversationId: Long): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.endConversation(conversationId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendMessage(conversationId: Long, messageText: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.sendMessage(conversationId, messageText).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createConversation(sellerId: Long, title: String): Conversation? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.createConversation(sellerId, title).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun startConversationWithCustomerService(): Conversation? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.startConversationWithCustomerService().execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMessages(conversationId: Long): List<Message>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getMessages(conversationId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}