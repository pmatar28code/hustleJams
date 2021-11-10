package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.GetPlaylistItemsApi
import com.example.hustlejams.networking.networkClasses.GetPlaylistItems
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GetPlaylistItemsNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        //.addNetworkInterceptor(interceptor)
        //.authenticator(authenticator)
        .build()

    private val getPlaylistItems: GetPlaylistItemsApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetPlaylistItemsApi::class.java)
        }

    private class UserCallback(
        private val onSuccess: (GetPlaylistItems) -> Unit
    ) : Callback<GetPlaylistItems> {
        override fun onResponse(call: Call<GetPlaylistItems>, response: Response<GetPlaylistItems>) {
            val playlistItems = GetPlaylistItems(
                href= response.body()?.href,
                        items = response.body()?.items,
                        limit= response.body()?.limit,
                        next= response.body()?.next,
                        offset = response.body()?.offset,
                     previous= response.body()?.previous,
                        total = response.body()?.total
            )
            onSuccess(playlistItems)


        }

        override fun onFailure(call: Call<GetPlaylistItems>, t: Throwable) {
            Log.e("Get Playlist Items NETWORK FAILURE","$t")
        }

    }

    fun getPlaylistItems(onSuccess: (GetPlaylistItems) -> Unit){
        val token = Repository.token
        val tempPlayListId = Repository.playListId
        getPlaylistItems.getPlaylistItems("Bearer $token",tempPlayListId).enqueue(UserCallback(onSuccess))
    }
}