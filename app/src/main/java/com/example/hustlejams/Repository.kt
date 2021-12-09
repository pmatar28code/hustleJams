package com.example.hustlejams

import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import com.spotify.android.appremote.api.SpotifyAppRemote

object Repository {
    var token = ""
    var playListId = ""
    var searchString = ""
    var listOfAddedSongsFromSearch = mutableListOf<String>()
    var workoutTime = 0
    var timeForSongsAddedFromSearchList = mutableListOf<Int>()
    var trackIdForTime = ""
    var newPlaylistName = ""
    var newPlaylistDescription = ""
    var userId = ""
    var newlyCreatedPlaylistName =""
    var newlyCratedPlaylistId = ""
    var mSpotify: SpotifyAppRemote ?= null
    var lastFragment = ""
    var currentlyPlaying = false
    var listOfTrackNamesLastPlaying = mutableListOf<String>()
    var playlistIdForDeleteUnFollow = ""
    var lastWorkoutPlaying: WorkoutClass?= null

    fun convertJsonStringToGetPlayListSpecificClass(stringObj:String): GetPlaylistSpecific {
        val gson = Gson()
        return gson.fromJson(stringObj, GetPlaylistSpecific::class.java)
    }

    fun removeFromImageUrl(imageUrl:String):String{
        var urlString = imageUrl
        urlString = urlString.replace("url=","")
        urlString = urlString.replace(", width=640","")
        urlString = urlString.replace("Image(height=640, ","")
        urlString = urlString.replace(")","")
        return urlString
    }
}