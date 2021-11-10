package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.AddTrack
import com.example.hustlejams.networking.networkClasses.SearchTrack
import retrofit2.Call
import retrofit2.http.*

interface SearchTrackApi {
    @Headers("Accept: application/json")
    @GET("v1/search")

    fun searchTrack(
        @Header("Authorization") Authorization:String,
        @Query("query") query:String,
        @Query("type") type:String,
        @Query("market") market:String,
        @Query("limit") limit:Int
    ): Call<SearchTrack>
}