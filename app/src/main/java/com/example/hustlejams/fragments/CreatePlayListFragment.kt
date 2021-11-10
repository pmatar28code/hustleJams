package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.SearchAdapter
import com.example.hustlejams.databinding.FragmentCreatePlaylistBinding
import com.example.hustlejams.networking.networkCalls.AddTrackToPlaylistNetwork
import com.example.hustlejams.networking.networkCalls.SearchTrackNetwork
import com.example.hustlejams.networking.networkCalls.TrackDetailsNetwork
import com.example.hustlejams.networking.networkClasses.SearchTrack

class CreatePlayListFragment: Fragment(R.layout.fragment_create_playlist){
    private var artistNameSearch = ""
    var trackNameSearch = ""
    private var searchAdapter: SearchAdapter ?= null
    var listOfAddedSongsFromSearch = mutableListOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreatePlaylistBinding.bind(view)

        searchAdapter = SearchAdapter() {
            val uri = it.uri
            val trackDuration = it.duration_ms
            Repository.timeForSongsAddedFromSearchList.add(trackDuration!!)
            Log.e("TRACK DETAILS TIME IN ADAPTER LIST","${Repository.timeForSongsAddedFromSearchList}")

            /*
            Repository.trackIdForTime = it.id.toString()

            TrackDetailsNetwork.getTrackDetails { trackDetails ->
                Repository.timeForSongsAddedFromSearchList.add(trackDetails.duration_ms.toString())
            }
            Log.e("TRACK DETAILS TIME IN ADAPTER LIST","${Repository.timeForSongsAddedFromSearchList}")


             */

            val workoutTime = Repository.workoutTime
            var actualTimeInList = 0
            for(time in Repository.timeForSongsAddedFromSearchList){
                actualTimeInList+=time
            }
            val timeInMinutes = actualTimeInList/60000
            Log.e("TIME IN MINUTES ACTUAL and time of workout:","$timeInMinutes / $workoutTime" )
            if(timeInMinutes <= workoutTime) {
                Log.e(
                    "Actual time in ms Repository",
                    "${Repository.timeForSongsAddedFromSearchList}, total: $timeInMinutes"
                )
                listOfAddedSongsFromSearch.add(uri.toString())
                Log.e("List Of Songs with Added Songs:","$listOfAddedSongsFromSearch")

            }else{
                Repository.timeForSongsAddedFromSearchList.removeLast()
                var timeInList = 0
                for(time in Repository.timeForSongsAddedFromSearchList){
                    timeInList+=time
                }
                timeInList /= 60000
                var timeLeftForSong = Repository.workoutTime - timeInList
                Log.e("TRACK DETAILS TIME IN ADAPTER After Removed last","${Repository.timeForSongsAddedFromSearchList} total current time in list = $timeInList, time left to add song $timeLeftForSong")
                Toast.makeText(requireContext(),"You have reached workout time limit ${Repository.timeForSongsAddedFromSearchList}, total: $timeInMinutes",Toast.LENGTH_LONG).show()
            }
        }

        binding.apply {
            searchButton.setOnClickListener {
                getSearchEditTextInputAndSearchWithNetwork(binding) {
                    searchResultsRecyclerView.apply {
                        adapter = searchAdapter
                        layoutManager = LinearLayoutManager(requireContext())
                        searchAdapter?.submitList(it)
                    }
                }
            }

            createPlaylistFromSearchButton.setOnClickListener {
                Log.e("LIST OF ADDED SONGS FROM SEARGC:","${listOfAddedSongsFromSearch.toString()}")
                Repository.listOfAddedSongsFromSearch = listOfAddedSongsFromSearch
                AddTrackToPlaylistNetwork.addTrackToPlaylist {
                    Log.e("Updated LIST: ","$it")
                }
            }
        }
    }

    private fun getSearchEditTextInputAndSearchWithNetwork(binding:FragmentCreatePlaylistBinding, callBack:(List<SearchTrack.Tracks.Item>) -> Unit) {
        binding.apply {
            trackNameSearch = searchTrackNameTextInputLayout.editText?.text.toString()
            artistNameSearch = searchArtistNameTextInputLayout.editText?.text.toString()
            Repository.searchString = "$trackNameSearch + $artistNameSearch"

            SearchTrackNetwork.searchTrack { searchResult ->
                callBack(searchResult.tracks?.items as List<SearchTrack.Tracks.Item>)
            }
        }
    }
}