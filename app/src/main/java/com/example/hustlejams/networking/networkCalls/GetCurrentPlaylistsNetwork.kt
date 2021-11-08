package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.GetCurrentPlaylists
import com.example.hustlejams.networking.networkClasses.GetPlaylists
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetCurrentPlaylistsNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        //.addInterceptor(logger)
        //.addNetworkInterceptor(interceptor)
        //.authenticator(authenticator)
        .build()

    private val getCurrentPlaylists: GetCurrentPlaylists
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetCurrentPlaylists::class.java)
        }

    private class GetPlaylistsCallback(
        private val onSuccess: (GetPlaylists) -> Unit
    ) : Callback<GetPlaylists> {
        override fun onResponse(call: Call<GetPlaylists>, response: Response<GetPlaylists>) {
            val playlists = GetPlaylists(
                href = response.body()?.href,
                        items = response.body()?.items,
                        limit = response.body()?.limit,
                        next = response.body()?.next,
                         offset = response.body()?.offset,
                         previous = response.body()?.previous,
                         total = response.body()?.total
            )
            onSuccess(playlists)


        }

        override fun onFailure(call: Call<GetPlaylists>, t: Throwable) {
            Log.e("USER NETWORK FAILURE","$t")
        }



    }

    fun getPlaylists(onSuccess: (GetPlaylists) -> Unit){
        val token = Repository.token
        getCurrentPlaylists.getPlaylists("Bearer $token").enqueue(GetPlaylistsCallback(onSuccess))
    }
}