package com.example.hbv601g_t8
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val basicAuthInterceptor = BasicAuthInterceptor("user1", "user1")

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(basicAuthInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val networkingApi: NetworkingService = retrofit.create(NetworkingService::class.java)
}
