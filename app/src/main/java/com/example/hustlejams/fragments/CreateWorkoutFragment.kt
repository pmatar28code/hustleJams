package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.R
import com.example.hustlejams.databinding.FragmentCreateWorkoutBinding
import com.example.hustlejams.networking.networkCalls.AddTrackToPlaylistNetwork
import com.example.hustlejams.networking.networkCalls.CreatePlaylistNetwork
import com.example.hustlejams.networking.networkCalls.GetCurrentPlaylistsNetwork
import com.example.hustlejams.networking.networkCalls.GetUserNetwork
import com.example.hustlejams.networking.networkClasses.DeleteTrackFromPlaylistNetwork

class CreateWorkoutFragment: Fragment(R.layout.fragment_create_workout) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateWorkoutBinding.bind(view)

        GetUserNetwork.getUser { user ->
            Log.e("USER CALLBACK ON FRAG CREATE WORK","${user.id}")
        }

        //CreatePlaylistNetwork.createList { list ->
         //   Log.e("LIST CALLBACK","$list")
       // }

        //AddTrackToPlaylistNetwork.addTrackToPlaylist { addTrack ->
          //  Log.e("Track Added:","$addTrack")
      //  }

        //DeleteTrackFromPlaylistNetwork.deleteTrack { deleteTrack ->
          //  Log.e("DELETE TRACK TEST","$deleteTrack")
       // }

        GetCurrentPlaylistsNetwork.getPlaylists { playlists ->
            Log.e("PLAYLISTS NETWORK:", "$playlists")
        }

    }
}