package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.CurrentlyPlayingTrackApi
import com.example.hustlejams.networking.networkClasses.CurrentlyPlayingTrack
import com.example.hustlejams.oauth.AccessTokenProviderImp
import com.example.hustlejams.oauth.TokenAuthorizationInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object CurrentlyPlayingTrackNetwork {
    private val accessTokenProvider = AccessTokenProviderImp()
    private val tokenAuthorizationInterceptor = TokenAuthorizationInterceptor(accessTokenProvider)
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        //.addInterceptor(logger)
        .addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        // .authenticator(authenticator)
        .build()

    private val currentlyPlayingTrackApi: CurrentlyPlayingTrackApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CurrentlyPlayingTrackApi::class.java)
        }

    private class CurrentlyPlayingTrackCallback(
        private val onSuccess: (CurrentlyPlayingTrack) -> Unit
    ) : Callback<CurrentlyPlayingTrack> {
        override fun onResponse(call: Call<CurrentlyPlayingTrack>, response: Response<CurrentlyPlayingTrack>) {
            val actualUser = CurrentlyPlayingTrack(
                actions= response.body()?.actions,
                        context= response.body()?.context,
                        currentlyPlayingType= response.body()?.currentlyPlayingType,
                        isPlaying= response.body()?.isPlaying,
                        item= response.body()?.item,
                        progressMs= response.body()?.progressMs,
                        timestamp= response.body()?.timestamp
            )
            onSuccess(actualUser)


        }

        override fun onFailure(call: Call<CurrentlyPlayingTrack>, t: Throwable) {
            Log.e("CURRENT PLAYING TRACK NETWORK FAILURE","$t")
        }



    }

    fun getCurrentlyPlayingTrack(onSuccess: (CurrentlyPlayingTrack) -> Unit){
        val token = Repository.token
         currentlyPlayingTrackApi.getCurrentlyPlayingTrack(token).enqueue(CurrentlyPlayingTrackCallback(onSuccess))
    }
}