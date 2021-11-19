package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.CurrentlyPlayingTrack
import com.example.hustlejams.networking.networkClasses.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface CurrentlyPlayingTrackApi {
    @Headers("Accept: application/json")
    @GET("v1/me/player/currently-playing")
    fun getCurrentlyPlayingTrack(
        @Header("Authorization") token:String
    ): Call<CurrentlyPlayingTrack>
}