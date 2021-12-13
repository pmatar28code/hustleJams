package com.example.hustlejams.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.hustlejams.Repository
import com.example.hustlejams.networking.networkClasses.SearchTrack

class addSongsFragViewModel:ViewModel() {
    var currentSongsLiveData = MutableLiveData<List<SearchTrack.Tracks.Item>>()

    init {
        currentSongsLiveData.postValue(emptyList())
    }

    fun getCurrentSongsLiveData(){
        val latestCurrentSongsFromSearchList = Repository.listOfAddedSongsFromSearchObjectsRepo
        currentSongsLiveData.postValue(latestCurrentSongsFromSearchList)
    }
}