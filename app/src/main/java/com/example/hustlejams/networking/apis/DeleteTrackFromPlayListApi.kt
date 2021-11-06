package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.DeleteTrack
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface DeleteTrackFromPlayListApi {
    @Headers("Authorization: application/json")
    @HTTP(method = "DELETE", path = "v1/playlists/{playlist_id}/tracks", hasBody = true)//@DELETE("v1/playlists/{playlist_id}/tracks")
    fun deleteTrack(
        @Header("Authorization") Authorization:String,
        @Path("playlist_id") playListId:String,
        @Body tracks: Map<String,Any>
    ): Call<DeleteTrack>
}