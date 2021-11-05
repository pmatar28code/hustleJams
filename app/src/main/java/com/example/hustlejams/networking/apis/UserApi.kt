package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {
    @GET("v1/me")
    fun getUserSpotify(@Header ("Authorization") token:String
    ):Call<User>
}