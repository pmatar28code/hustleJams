package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.GetPlaylists
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface GetCurrentPlaylists {
    @Headers("Accept: application/json")
    @GET("v1/me/playlists")
    fun getPlaylists(
        @Header("Authorization") Authorization:String
    ): Call<GetPlaylists>

}