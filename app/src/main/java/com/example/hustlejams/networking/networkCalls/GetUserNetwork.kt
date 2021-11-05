package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.UserApi
import com.example.hustlejams.networking.networkClasses.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object GetUserNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        //.addInterceptor(logger)
        //.addNetworkInterceptor(interceptor)
        //.authenticator(authenticator)
        .build()

    private val user: UserApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UserApi::class.java)
        }

    private class UserCallback(
        private val onSuccess: (User) -> Unit
    ) : Callback<User> {
        override fun onResponse(call: Call<User>, response: Response<User>) {
            val actualUser = User(
                displayName = response.body()?.displayName,
                externalUrls = response.body()?.externalUrls,
                followers = response.body()?.followers,
                href = response.body()?.href,
                id = response.body()?.id,
                images = response.body()?.images,
                type = response.body()?.type,
                uri = response.body()?.uri
            )
            onSuccess(actualUser)


        }

        override fun onFailure(call: Call<User>, t: Throwable) {
            Log.e("USER NETWORK FAILURE","$t")
        }



    }

    fun getUser(onSuccess: (User) -> Unit){
        val token = Repository.token
        user.getUserSpotify("Bearer $token").enqueue(UserCallback(onSuccess))
    }
}