package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.AddTrack
import retrofit2.Call
import retrofit2.http.*
//@JvmSuppressWildcards
interface AddTrackToPlaylistApi {
    @Headers("Accept: application/json")
    @POST("v1/playlists/{playlist_id}/tracks")
   // @FormUrlEncoded
    fun addTrackToPlaylist(
        @Header("Authorization") Authorization:String,
        @Path("playlist_id") playList_id:String,
        @Query("position") position:Int,
        @Query("uris") uris:String
    ): Call<AddTrack>

}