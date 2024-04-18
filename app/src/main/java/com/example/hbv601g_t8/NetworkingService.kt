package com.example.hbv601g_t8

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkingService {

    @GET("/api/chat")
    fun getAllConversations(): Call<List<Conversation>>

    @GET("/api/chat/{id}")
    fun getConversation(@Path("id") conversationId: Long): Call<Conversation>

    @POST("/api/chat/end/{id}")
    fun endConversation(@Path("id") conversationId: Long): Call<String>

    @POST("/api/chat/send/{id}")
    fun sendMessage(@Path("id") conversationId: Long, @Query("message") messageText: String): Call<String>

    @POST("/api/chat/create/{sellerId}/{title}")
    fun createConversation(@Path("sellerId") sellerId: Long, @Path("title") title: String): Call<Conversation>

    @POST("/api/chat/customer-service")
    fun startConversationWithCustomerService(): Call<Conversation>

    @GET("/messages/{conversationId}")
    fun getMessages(@Path("conversationId") conversationId: Long): Call<List<Message>>


    //

    @POST("/api/account/changename")
    fun changeUserName(@Query("name") newName: String): Call<String>

    @POST("/api/account/changeemail")
    fun changeUserEmail(@Query("old-email") oldEmail: String, @Query("new-email") newEmail: String): Call<String>

    @POST("/api/account/changepass")
    fun changeUserPassword(@Query("old-pass") oldPass: String, @Query("new-pass") newPass: String, @Query("confirm-pass") confirmPass: String): Call<String>

    @POST("/api/account/delete")
    fun deleteUserAccount(@Query("pass") password: String): Call<String>

    //

    @POST("/api/users/login")
    fun loginUser(@Body user: User): Call<String>

    @POST("/api/users/signup")
    fun signupUser(@Body user: User): Call<User>

    //

    @GET("/api/disc/all")
    fun getAllDiscs(): Call<List<Disc>>

    @GET("/mydiscs/{userId}")
    fun getMyDiscs(@Path ("userId") userId: Long): Call<List<Disc>>

    @GET("/api/disc/home/favorites")
    fun getFavoriteDiscs(): Call<List<Disc>>

    @POST("/api/disc/addDisc")
    @Multipart
    fun addDisc(@Part("disc") disc: Disc): Call<Disc>

    @POST("/api/disc/update/{id}")
    @Multipart
    fun updateDisc(@Path("id") id: Long, @Part("disc") disc: Disc): Call<String>

    @DELETE("/api/disc/{id}")
    fun deleteDisc(@Path("id") id: Long): Call<String>

    @GET("/api/disc/{id}")
    fun getDisc(@Path("id") id: Long): Call<Disc>

    @DELETE("/api/disc/{id}/images/{imageId}")
    fun deleteImage(@Path("id") discId: Long, @Path("imageId") imageId: Long): Call<String>

    @POST("/api/disc/favorites/{discId}")
    fun addToFavorites(@Path("discId") discId: Long): Call<String>

    @DELETE("/api/disc/favorite/{discId}")
    fun removeFromFavorites(@Path("discId") discId: Long): Call<String>

    @GET("/api/disc/isFavorite/{discId}")
    fun isDiscFavorite(@Path("discId") discId: Long): Call<Boolean>

    @GET("/api/disc/filter")
    fun filterDiscs(
        @Query("fromPrice") fromPrice: Int?,
        @Query("toPrice") toPrice: Int?,
        @Query("type") type: String?,
        @Query("colour") colour: String?,
        @Query("condition") condition: String?,
        @Query("name") name: String?
    ): Call<List<Disc>>
}