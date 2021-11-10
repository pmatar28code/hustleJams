package com.example.hustlejams

object Repository {
    var token = ""
    var playListId = ""
    var searchString = ""
    var listOfAddedSongsFromSearch = mutableListOf<String>()
    var workoutTime = 0
    var timeForSongsAddedFromSearchList = mutableListOf<Int>()
    var trackIdForTime = ""
}