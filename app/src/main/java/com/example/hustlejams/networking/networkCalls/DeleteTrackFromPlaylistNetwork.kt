package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.DeleteTrackFromPlayListApi
import com.example.hustlejams.networking.networkClasses.DeleteTrack
import com.example.hustlejams.oauth.AccessTokenProviderImp
import com.example.hustlejams.oauth.TokenAuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DeleteTrackFromPlaylistNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor = TokenAuthorizationInterceptor(accessTokenProvider)
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val client = OkHttpClient.Builder()
       // .addNetworkInterceptor(tokenAuthorizationInterceptor)
        .addInterceptor(logger)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val deleteTrackApi : DeleteTrackFromPlayListApi
        get() {
            return retrofit.create(DeleteTrackFromPlayListApi::class.java)
        }

    private class DeleteTrackCallBack(private val onSuccess: (DeleteTrack) -> Unit
    ): Callback<DeleteTrack> {
        override fun onResponse(call: Call<DeleteTrack>, response: Response<DeleteTrack>) {
            val deleteTrack = DeleteTrack(
                snapshotId = response.body()?.snapshotId
            )
            onSuccess(deleteTrack)
        }

        override fun onFailure(call: Call<DeleteTrack>, t: Throwable) {
            Log.e("Delete Track from PL Network Failure","$t")
        }

    }

    fun deleteTrack(onSuccess: (DeleteTrack) -> Unit){
        val token = Repository.token
        val playListTempId = "6RqsezSxbIb28Sqsw6NfS9"
        val tracks = mutableMapOf<String,Any>()
        val listOfUriMap = mutableListOf<MutableMap<String,String>>()
        val uriMap = mutableMapOf<String,String>()
        uriMap["uri"] = "spotify:track:0GeezbrS87YZgXuyksdg2q"
        listOfUriMap.add(uriMap)
        tracks["tracks"] = listOfUriMap
        deleteTrackApi.deleteTrack("Bearer $token",playListTempId,tracks).enqueue(
            DeleteTrackCallBack(onSuccess)
        )

    }
}