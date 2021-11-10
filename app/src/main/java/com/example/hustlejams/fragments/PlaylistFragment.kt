package com.example.hustlejams.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.R
import com.example.hustlejams.adapters.PlaylistsAdapter
import com.example.hustlejams.databinding.FragmentPlaylistBinding
import com.example.hustlejams.networking.networkCalls.GetCurrentPlaylistsNetwork

class PlaylistFragment: Fragment(R.layout.fragment_playlist){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentPlaylistBinding.bind(view)

        val playListAdapter = PlaylistsAdapter(){
            Toast.makeText(requireContext(),"Testing click on playlist ${it.name}",Toast.LENGTH_SHORT).show()
        }
        GetCurrentPlaylistsNetwork.getPlaylists {
            binding.recyclerViewPlaylists.apply {
                adapter = playListAdapter
                layoutManager = LinearLayoutManager(requireContext())

                playListAdapter.submitList(it.items)
            }

        }




    }
}