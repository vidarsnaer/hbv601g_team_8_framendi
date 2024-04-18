package com.example.hbv601g_t8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService {

    suspend fun loginUser(user: User): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.loginUser(user).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun signupUser(user: User): User? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.signupUser(user).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }


}
