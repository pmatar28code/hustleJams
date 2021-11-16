package com.example.hustlejams

import com.spotify.android.appremote.api.AppRemote
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
    var playListIdPlayListSpecific = ""
    var newlyCreatedPlaylistName =""
    var newlyCratedPlaylistId = ""
    var mSpotify: SpotifyAppRemote ?= null

}