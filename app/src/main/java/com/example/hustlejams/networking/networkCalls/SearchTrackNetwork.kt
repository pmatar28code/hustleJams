package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.SearchTrackApi
import com.example.hustlejams.networking.networkClasses.SearchTrack
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchTrackNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val client = OkHttpClient.Builder()
        //.addInterceptor(logger)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val searchTrackApi : SearchTrackApi
        get() {
            return retrofit.create(SearchTrackApi::class.java)
        }

    private class SearchTrackCallBack(private val onSuccess: (SearchTrack) -> Unit
    ): Callback<SearchTrack> {
        override fun onResponse(call: Call<SearchTrack>, response: Response<SearchTrack>) {
            val searchTrack = SearchTrack(
                tracks = response.body()?.tracks
            )
            if(searchTrack.tracks?.items?.size != 0) {
                Log.e(
                    "TRack duration example NETWORk",
                    "${searchTrack.tracks?.items?.get(0)?.duration_ms}"
                )

                onSuccess(searchTrack)
            }else{
                searchTrack.tracks.items = emptyList()
                onSuccess(searchTrack)
            }
        }

        override fun onFailure(call: Call<SearchTrack>, t: Throwable) {
            Log.e("SEARCH TRACK TO NETWORK FAILURE","$t")
        }

    }

    fun searchTrack(onSuccess: (SearchTrack) -> Unit){
        val token = Repository.token
        val market = "US"
        val limit = 50
        val type = "track"

        val query = Repository.searchString//"sober + tool"
        searchTrackApi.searchTrack("Bearer $token",query,type,market,limit).enqueue(SearchTrackCallBack(onSuccess))
    }
}