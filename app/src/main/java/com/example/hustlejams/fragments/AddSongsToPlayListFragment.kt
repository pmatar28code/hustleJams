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
import com.example.hustlejams.databinding.FragmentAddSongsToPlaylistBinding
import com.example.hustlejams.fragments.dialogs.CurrentSongsAddedFromSearchDialogFragment
import com.example.hustlejams.networking.networkCalls.AddTrackToPlaylistNetwork
import com.example.hustlejams.networking.networkCalls.SearchTrackNetwork
import com.example.hustlejams.networking.networkClasses.SearchTrack

class AddSongsToPlayListFragment: Fragment(R.layout.fragment_add_songs_to_playlist){
    private var artistNameSearch = ""
    var trackNameSearch = ""
    private var searchAdapter: SearchAdapter ?= null
    //var listOfAddedSongsFromSearch = mutableListOf<String>()
    val listOfAddedSongsFromSearchObjects = mutableListOf<SearchTrack.Tracks.Item>()
    var timeLeftAtClickToAddToList = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddSongsToPlaylistBinding.bind(view)

        val playListNameFromWorkoutFragment = arguments?.getString("name")
        val playlistIdFromWourkoutFragment = arguments?.getString("playlistId")
        val workoutName = arguments?.getString("workoutName")
        val workoutTime = arguments?.getString("workoutTime")

        Repository.newlyCratedPlaylistId = playlistIdFromWourkoutFragment.toString()

        searchAdapter = SearchAdapter() {
            binding.searchTrackNameTextInputLayout.editText?.text?.clear()
            binding.searchArtistNameTextInputLayout.editText?.text?.clear()
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
           // val timeInMinutesRoundedCeil = ceil(timeInMinutes)
            Log.e("Time in minutes to double:","$timeInMinutes")
            var timeLeft = (Repository.workoutTime - timeInMinutes)
            //val roundedTimeLeft = ceil(timeLeft)


            if(timeInMinutes <= Repository.workoutTime || timeInMinutes <= Repository.workoutTime.plus(.35)){
                //listOfAddedSongsFromSearch.add(uri.toString())
                listOfAddedSongsFromSearchObjects.add(it)
                Repository.listOfAddedSongsFromSearchObjectsRepo = listOfAddedSongsFromSearchObjects
                Log.e("ListOfAddedSongsFromSearch","$listOfAddedSongsFromSearchObjects")
                Log.e("ListOfTimeIFTRUE","${Repository.timeForSongsAddedFromSearchList}")
                binding.searchPlaylistTimeLeftToAddSong.text = timeLeft.toString()
            }else{
                Repository.timeForSongsAddedFromSearchList.removeLast()
                Log.e("ListOfTimeElse","${Repository.timeForSongsAddedFromSearchList}")
                Toast.makeText(requireContext(),"You cannot add more songs, workout time exceeded",Toast.LENGTH_SHORT).show()
            }

        }

        binding.apply {
            addSongsPlaylistName.text = playListNameFromWorkoutFragment
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

            addSongsToPlaylistFromSearchButton.setOnClickListener {
                val timeLeftTextViewText = binding.searchPlaylistTimeLeftToAddSong.text
                if(timeLeftTextViewText != "playlist time left" && timeLeftTextViewText != null ) {
                    timeLeftAtClickToAddToList =
                        binding.searchPlaylistTimeLeftToAddSong.text.toString().toDouble()

                }else{
                    timeLeftAtClickToAddToList = Repository.workoutTime.toDouble()
                }
                if(timeLeftAtClickToAddToList < .35) {
                    Log.e(
                        "LIST OF ADDED SONGS FROM SEARGC:",listOfAddedSongsFromSearchObjects.toString()
                    )
                    Toast.makeText(requireContext(),"Songs Added Successfully",Toast.LENGTH_SHORT).show()
                    //Repository.listOfAddedSongsFromSearch = listOfAddedSongsFromSearch

                    for(song in Repository.listOfAddedSongsFromSearchObjectsRepo){
                        Repository.listOfAddedSongsFromSearch.add(song.uri.toString())
                    }

                    AddTrackToPlaylistNetwork.addTrackToPlaylist {
                        //set playlist or get from it was set
                        Log.e("Updated LIST: ", "${it.snapshotId}")
                    }

                    Repository.timeForSongsAddedFromSearchList.clear()
                    //
                    Repository.listOfAddedSongsFromSearch.clear()
                    Repository.listOfAddedSongsFromSearchObjectsRepo.clear()


                    val fragManager = parentFragmentManager
                    val createWorkoutFragment = CreateWorkoutFragment()
                    val args =  Bundle()
                    args.putString("playlistWithAddedSongs", binding.addSongsPlaylistName.text.toString())
                    args.putString("workoutName",workoutName)
                    args.putString("workoutTime",workoutTime)
                    createWorkoutFragment.arguments = args
                    fragManager.beginTransaction()
                        .replace(R.id.fragment_container_main,createWorkoutFragment)
                        .addToBackStack("back")
                        .commit()
                    //Take to workout fragment from here.
                }else{
                    Toast.makeText(requireContext(),"Please add a song with length of: $timeLeftAtClickToAddToList",Toast.LENGTH_LONG).show()
                }
            }

            binding.viewCurrentlySelectedSongsFromSearch.setOnClickListener {
                CurrentSongsAddedFromSearchDialogFragment.create {
                    //var currentTimeInTextView = binding.searchPlaylistTimeLeftToAddSong.text.toString().toDouble()
                    var timeInList = 0
                    for(time in Repository.timeForSongsAddedFromSearchList){
                        timeInList+=time
                    }
                    val timeInMinutes = (timeInList/60000.00)
                    // val timeInMinutesRoundedCeil = ceil(timeInMinutes)
                    Log.e("xxxxxxxx:","$timeInMinutes")
                    val timeLeft = (Repository.workoutTime - timeInMinutes)
                    binding.searchPlaylistTimeLeftToAddSong.text = timeLeft.toString()
                }.show(parentFragmentManager,"Show current songs selected from search")
            }
        }
    }

    private fun getSearchEditTextInputAndSearchWithNetwork(binding:FragmentAddSongsToPlaylistBinding, callBack:(List<SearchTrack.Tracks.Item>) -> Unit) {
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