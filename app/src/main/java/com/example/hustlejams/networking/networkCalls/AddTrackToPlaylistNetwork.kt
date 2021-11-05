package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.AddTrackToPlaylistApi
import com.example.hustlejams.networking.networkClasses.AddTrack
import com.example.hustlejams.networking.networkClasses.User
import com.example.hustlejams.oauth.AccessTokenProvider
import com.example.hustlejams.oauth.AccessTokenProviderImp
import com.example.hustlejams.oauth.TokenAuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AddTrackToPlaylistNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor = TokenAuthorizationInterceptor(accessTokenProvider)
    private val logger = HttpLoggingInterceptor()
           .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val client = OkHttpClient.Builder()
        .addNetworkInterceptor(tokenAuthorizationInterceptor)
        .addInterceptor(logger)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val addTrackApi : AddTrackToPlaylistApi
    get() {
        return retrofit.create(AddTrackToPlaylistApi::class.java)
    }

    private class AddTrackCallBack(private val onSuccess: (AddTrack) -> Unit
    ):Callback<AddTrack>{
        override fun onResponse(call: Call<AddTrack>, response: Response<AddTrack>) {
            val addTrack = AddTrack(
                snapshotId = response.body()?.snapshotId
            )
            onSuccess(addTrack)
        }

        override fun onFailure(call: Call<AddTrack>, t: Throwable) {
            Log.e("ADDTRACK TO PLAYLIST NETWORK FAILURE","$t")
        }

    }

    fun addTrackToPlaylist(onSuccess: (AddTrack) -> Unit){
        val token = Repository.token
        val tempPlayListId = "6RqsezSxbIb28Sqsw6NfS9"
        val data = mutableMapOf<String,Any>()
        data["position"] = "2"
        data["uris"] = "spotify:track:2VeYKsOmvGnN9Wd0RTDPuC"
        val position = 4
        val uris =  "spotify:track:0GeezbrS87YZgXuyksdg2q"
        addTrackApi.addTrackToPlaylist("Bearer $token",tempPlayListId,position,uris).enqueue(AddTrackCallBack(onSuccess))
    }
}