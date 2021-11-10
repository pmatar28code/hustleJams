package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.TrackDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface TrackDetailsApi {
    @Headers("Accept: application/json")
    @GET("v1/tracks/{track_id}")
    fun getTrackDetails(
        @Header("Authorization") authorization:String,
        @Path("track_id") track_id:String
    ): Call<TrackDetails>

}