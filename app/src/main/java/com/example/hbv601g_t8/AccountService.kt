package com.example.hbv601g_t8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AccountService {


    suspend fun changeUserName(newName: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.changeUserName(newName).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeUserEmail(oldEmail: String, newEmail: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.changeUserEmail(oldEmail, newEmail).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeUserPassword(oldPass: String, newPass: String, confirmPass: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.changeUserPassword(oldPass, newPass, confirmPass).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteUserAccount(password: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.deleteUserAccount(password).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
