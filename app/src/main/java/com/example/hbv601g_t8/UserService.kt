package com.example.hbv601g_t8

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService {

    suspend fun loginUser(userName: String, userPassword: String): Boolean = withContext(Dispatchers.IO) {
        val call = RetrofitClient.networkingApi.loginUser(userName, userPassword)
        try {
            val response = call.execute() // Executes the network call synchronously on the background thread
            if (response.isSuccessful) {
                Log.d("UserService", "Response: ${response.body()}")
                return@withContext response.body()?.contains("Successfully logged in") == true
            } else {
                Log.e("UserService", "Login failed with response: ${response.errorBody()?.string()}")
                return@withContext false
            }
        } catch (e: Exception) {
            Log.e("UserService", "Login request failed: $e")
            return@withContext false
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
