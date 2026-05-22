package com.tvlive.player.api

import com.tvlive.player.model.Channel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("api/channels")
    fun getChannels(): Call<List<Channel>>

    @POST("api/channels/batch")
    fun uploadChannels(@Body channels: List<Channel>): Call<ApiResponse>
}

data class ApiResponse(val success: Boolean, val message: String)
