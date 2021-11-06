package com.example.hustlejams.networking.networkCalls

import android.util.Log
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.apis.CreatePlayListApi
import com.example.hustlejams.networking.networkClasses.CreateList
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object CreatePlaylistNetwork {
    private val logger = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BODY )
    val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        //.addNetworkInterceptor(interceptor)
        //.authenticator(authenticator)
        .build()

    private val createPlayListApi: CreatePlayListApi
        get() {
            return Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CreatePlayListApi::class.java)
        }

    private class CreatePlaylistCallback(
        private val onSuccess: (CreateList) -> Unit
    ) : Callback<CreateList> {
        override fun onResponse(call: Call<CreateList>, response: Response<CreateList>) {
            Log.e("LIST RESPONSE:","${response.body()?.name}")
            val actualList = CreateList(
                collaborative = response.body()?.collaborative,
                description =response.body()?.description,
                externalUrls =response.body()?.externalUrls,
                followers = response.body()?.followers,
                href = response.body()?.href,
                id = response.body()?.id,
                images = response.body()?.images,
                name= response.body()?.name,
                owner= response.body()?.owner,
                primaryColor= response.body()?.primaryColor,
                public= response.body()?.public,
                snapshotId=response.body()?.snapshotId,
                tracks = response.body()?.tracks,
                type = response.body()?.type,
                uri = response.body()?.uri
            )
            onSuccess(actualList)


        }

        override fun onFailure(call: Call<CreateList>, t: Throwable) {
            Log.e("CreateList NETWORK FAILURE","$t")
        }



    }

    fun createList(onSuccess: (CreateList) -> Unit){
        val data = mutableMapOf<String,String>()
        data["name"] = "New List Confirming"
        data["description"] = "testing description new confirming"
        data["public"] = "false"

        val token = Repository.token
        createPlayListApi.createPlaylist("Bearer $token","fakepeterson1",data).enqueue(CreatePlaylistCallback(onSuccess))
    }
}