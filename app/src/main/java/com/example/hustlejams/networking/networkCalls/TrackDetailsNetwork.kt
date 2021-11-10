package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.TrackDetailsApi
import com.example.hustlejams.networking.networkClasses.TrackDetails
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TrackDetailsNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.spotify.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackDetailsApi : TrackDetailsApi
        get() {
            return retrofit.create(TrackDetailsApi::class.java)
            }

    private class TrackDetailsCallBack(private val onSuccess: (TrackDetails) -> Unit
    ): Callback<TrackDetails> {
        override fun onResponse(call: Call<TrackDetails>, response: Response<TrackDetails>) {
            val trackDetails = TrackDetails(
                album = response.body()?.album,
                artists =response.body()?.artists,
                discNumber=response.body()?.discNumber,
                duration_ms=response.body()?.duration_ms,
                explicit=response.body()?.explicit,
                externalIds=response.body()?.externalIds,
                externalUrls=response.body()?.externalUrls,
                href=response.body()?.href,
                id=response.body()?.id,
                isLocal=response.body()?.isLocal,
                isPlayable=response.body()?.isPlayable,
                name=response.body()?.name,
                popularity=response.body()?.popularity,
                previewUrl=response.body()?.previewUrl,
                trackNumber=response.body()?.trackNumber,
                type=response.body()?.type,
                uri=response.body()?.uri,
            )
            Log.e("TRack Details duration Network","${trackDetails.duration_ms}")

            onSuccess(trackDetails)
        }

        override fun onFailure(call: Call<TrackDetails>, t: Throwable) {
            Log.e("Track DETAILS NETWORK FAILURE","$t")
        }

    }

    fun getTrackDetails(onSuccess: (TrackDetails) -> Unit){
        val token = Repository.token
        val trackId = Repository.trackIdForTime

        trackDetailsApi.getTrackDetails("Bearer $token",trackId).enqueue(TrackDetailsCallBack(onSuccess))
    }
}