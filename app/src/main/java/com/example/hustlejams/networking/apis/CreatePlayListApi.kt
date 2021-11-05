package com.example.hustlejams.networking.apis

import com.example.hustlejams.networking.networkClasses.CreateList
import retrofit2.Call
import retrofit2.http.*

/*
curl -X "POST" "https://api.spotify.com/v1/users/fakepeterson1/playlists" --data "{\"name\":\"test1\",\"description\":\"test1 description\",\"public\":false}" -H "Accept: application/json" -H "Content-Type: application/json" -H "Authorization: Bearer BQCYGpGhqVbxhZ9LQiaW7i6PsqrBIgykQ1Hz448G0ouYeqekNmYf6Mb43jT5PCe426rbb2uDuxxH2nMZvxqMP3yYMN0KZqk-41kU5l0w65xw2MU2uNku1C2HYCW0ojBiwGimMvLRhvqz5tneAJ_Wpg3yX40VfiWH8dGjSY4rLnJRNIJjeChQzaLerXOj4zcfct-tFhlmUjb176oM4QRDkLVudFD53OFR_XW8ijpn6Y09iklde8cIEzJgj4V0iCt3fH8RVvxp_k4G-cgFsEaIEv-yqKI"
 */
@JvmSuppressWildcards
interface CreatePlayListApi {
    @Headers("Accept: application/json")
    @POST("v1/users/{user_id}/playlists")
   // @FormUrlEncoded
    fun createPlaylist(
        @Header("Authorization") authorization:String,
        @Path("user_id") user_id:String,
        @Body data: Map<String,Any>
    ): Call<CreateList>
}