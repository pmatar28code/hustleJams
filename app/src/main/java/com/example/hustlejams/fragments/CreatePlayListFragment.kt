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
import com.example.hustlejams.networking.networkClasses.SearchTrack
import kotlin.math.ceil

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
            Log.e("URI","$uri")
            val trackDuration = it.duration_ms
            Log.e("track clicked duration:", trackDuration.toString())
            Repository.timeForSongsAddedFromSearchList.add(trackDuration!!)
            Log.e("Current times in timeforsongsaddedetc","${Repository.timeForSongsAddedFromSearchList}")
            var timeInList = 0

            for(time in Repository.timeForSongsAddedFromSearchList){
                timeInList+=time
            }

            var timeInMinutes = (timeInList/60000.00)
            val timeInMinutesRoundedCeil = ceil(timeInMinutes)
            Log.e("Time in minutes to double:","$timeInMinutes")
            var timeLeft = (Repository.workoutTime - timeInMinutes)
            val roundedTimeLeft = ceil(timeLeft)


            if(timeInMinutes <= Repository.workoutTime || timeInMinutes <= Repository.workoutTime.plus(.35)){
                listOfAddedSongsFromSearch.add(uri.toString())
                Log.e("ListOfAddedSongsFromSearch","$listOfAddedSongsFromSearch")
                Log.e("ListOfTimeIFTRUE","${Repository.timeForSongsAddedFromSearchList}")
                binding.searchPlaylistTimeLeftToAddSong.text = timeLeft.toString()
            }else{
                Repository.timeForSongsAddedFromSearchList.removeLast()
                Log.e("ListOfTimeElse","${Repository.timeForSongsAddedFromSearchList}")
            }

        }

        binding.apply {
            searchWorkoutTotalTime.text = Repository.workoutTime.toString()

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
                val timeLeftAtClickToAddToList = binding.searchPlaylistTimeLeftToAddSong.text.toString().toDouble()
                if(timeLeftAtClickToAddToList < .35) {
                    Log.e(
                        "LIST OF ADDED SONGS FROM SEARGC:",listOfAddedSongsFromSearch.toString()
                    )
                    Repository.listOfAddedSongsFromSearch = listOfAddedSongsFromSearch
                    AddTrackToPlaylistNetwork.addTrackToPlaylist {
                        //set playlist or get from it was set
                        Log.e("Updated LIST: ", "${it.snapshotId}")
                    }
                    //Take to worjout fragment from here.
                }else{
                    Toast.makeText(requireContext(),"Please add a song with length of: $timeLeftAtClickToAddToList",Toast.LENGTH_LONG).show()
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