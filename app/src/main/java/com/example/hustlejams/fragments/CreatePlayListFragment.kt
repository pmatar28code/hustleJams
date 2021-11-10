package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.SearchAdapter
import com.example.hustlejams.databinding.FragmentCreatePlaylistBinding
import com.example.hustlejams.networking.networkCalls.AddTrackToPlaylistNetwork
import com.example.hustlejams.networking.networkCalls.CreatePlaylistNetwork
import com.example.hustlejams.networking.networkCalls.SearchTrackNetwork
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
            Log.e("SEARCH ADAPTER XX","$uri")
            listOfAddedSongsFromSearch.add(uri.toString())
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