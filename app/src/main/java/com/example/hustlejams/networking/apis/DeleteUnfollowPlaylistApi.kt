package com.example.hustlejams.networking.apis

import retrofit2.Call
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path

@JvmSuppressWildcards
interface DeleteUnfollowPlaylistApi {
    @Headers("Authorization: application/json")
    @HTTP(method = "DELETE", path = "v1/playlists/{playlist_id}/followers", hasBody = false)//@DELETE("v1/playlists/{playlist_id}/tracks")
    fun deletePlaylist(
        @Header("Authorization") Authorization:String,
        @Path("playlist_id") playListId:String,
        //@Body followers: Map<String,Any>
    ): Call<Boolean>
}
