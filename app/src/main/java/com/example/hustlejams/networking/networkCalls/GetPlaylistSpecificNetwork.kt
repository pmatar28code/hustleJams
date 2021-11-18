package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.GetPlaylistSpecificApi
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object GetPlaylistSpecificNetwork {
   // private val accessTokenProvider = AccessTokenProviderImp()
    //private val tokenAuthorizationInterceptor = TokenAuthorizationInterceptor(accessTokenProvider)
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        .protocols(Collections.singletonList(Protocol.HTTP_1_1))
        .addInterceptor(logger)
        //.addNetworkInterceptor(TokenAuthorizationInterceptor(accessTokenProvider))
        // .authenticator(authenticator)
        .build()

    private val playlistSpecific: GetPlaylistSpecificApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GetPlaylistSpecificApi::class.java)
        }

    private class PlaylistSpecificCallback(
        private val onSuccess: (GetPlaylistSpecific) -> Unit
    ) : Callback<GetPlaylistSpecific> {
        override fun onResponse(call: Call<GetPlaylistSpecific>, response: Response<GetPlaylistSpecific>) {
            val playlistSpecific = GetPlaylistSpecific(
                collaborative = response.body()?.collaborative,
                description =  response.body()?.description,
                externalUrls = response.body()?.externalUrls,
                followers = response.body()?.followers,
                href = response.body()?.href,
                id = response.body()?.id,
                images = response.body()?.images,
                name = response.body()?.name,
                owner = response.body()?.owner,
                primaryColor = response.body()?.primaryColor,
                public = response.body()?.public,
                snapshotId = response.body()?.snapshotId,
                tracks = response.body()?.tracks,
                type = response.body()?.type,
                uri = response.body()?.uri
            )
            onSuccess(playlistSpecific)


        }

        override fun onFailure(call: Call<GetPlaylistSpecific>, t: Throwable) {
            Log.e("USER NETWORK FAILURE","$t")
        }



    }

    fun getPlaylistSpecific(onSuccess: (GetPlaylistSpecific) -> Unit){
        val token = Repository.token
        val playlistId = Repository.newlyCratedPlaylistId
        playlistSpecific.getPlaylistSpecific("Bearer $token",playlistId).enqueue(PlaylistSpecificCallback(onSuccess))
    }
}