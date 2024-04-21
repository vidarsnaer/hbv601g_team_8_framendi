package com.example.hbv601g_t8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscService {

    suspend fun getAllDiscs(): List<Disc>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getAllDiscs().execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMyDiscs(userId: Long): List<Disc>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getMyDiscs(userId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getFavoriteDiscs(): List<Disc>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getFavoriteDiscs().execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isDiscFavorite(discId: Long): Boolean? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.isDiscFavorite(discId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addDisc(disc: Disc): Disc? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.addDisc(disc).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateDisc(id: Long, disc: Disc): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.updateDisc(id, disc).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteDisc(id: Long): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.deleteDisc(id).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDisc(id: Long): Disc? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.getDisc(id).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleteImage(discId: Long, imageId: Long): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.deleteImage(discId, imageId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addToFavorites(discId: Long): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.addToFavorites(discId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun removeFromFavorites(discId: Long): String? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.removeFromFavorites(discId).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun filterDiscs(fromPrice: Int?, toPrice: Int?, type: String?, colour: String?, condition: String?, name: String?): List<Disc>? = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.networkingApi.filterDiscs(fromPrice, toPrice, type, colour, condition, name).execute()
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }
}
