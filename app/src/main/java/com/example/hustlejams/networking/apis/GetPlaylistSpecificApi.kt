package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

interface GetPlaylistSpecificApi {
    @Headers("Accept: application/json")
    @GET("v1/playlists/{playlist_id}")
    fun getPlaylistSpecific(
        @Header("Authorization:") Authorization:String,
        @Path("playlist_id") playlist_id:String
    ): Call<GetPlaylistSpecific>
}