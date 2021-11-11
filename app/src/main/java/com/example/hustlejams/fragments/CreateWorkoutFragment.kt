package com.example.hustlejams.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.databinding.FragmentCreateWorkoutBinding
import com.example.hustlejams.fragments.dialogs.CreatePlayListDialogFragment

class CreateWorkoutFragment: Fragment(R.layout.fragment_create_workout) {
    //var createWorkoutAdapter:
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateWorkoutBinding.bind(view)



        binding.apply {
            createPlaylistButton.setOnClickListener {
                CreatePlayListDialogFragment.create {
                    Toast.makeText(requireContext()," ${Repository.newPlaylistName} List Successfully created",Toast.LENGTH_SHORT).show()
                    //submit list
                }.show(parentFragmentManager,"open create playlist dialog")
            }


            addSongsToPlaylistButton.setOnClickListener {
                val timeTextInput = timeTextInputLayout.editText?.text.toString()
                if(timeTextInput != "") {
                    Repository.workoutTime = timeTextInputLayout.editText?.text.toString().toInt()
                }

                val fragManager = parentFragmentManager
                fragManager.beginTransaction()
                    .replace(R.id.fragment_container_main,AddSongsToPlayListFragment())
                    .addToBackStack("back")
                    .commit()
            }
        }

       // GetUserNetwork.getUser { user ->
        //    Log.e("USER CALLBACK ON FRAG CREATE WORK","${user.id}")
        //}

        //CreatePlaylistNetwork.createList { list ->
         //   Log.e("LIST CALLBACK","$list")
       // }

        //AddTrackToPlaylistNetwork.addTrackToPlaylist { addTrack ->
            //Log.e("Track Added:","$addTrack")
        //}

        //DeleteTrackFromPlaylistNetwork.deleteTrack { deleteTrack ->
          //  Log.e("DELETE TRACK TEST","$deleteTrack")
       // }
       /*
       GetCurrentPlaylistsNetwork.getPlaylists { playlists ->
            Log.e("PLAYLISTS NETWORK:", "$playlists")
           val testPlaylistId = playlists.items?.get(2)?.id
           Repository.playListId = testPlaylistId.toString()

           GetPlaylistItemsNetwork.getPlaylistItems {
               Log.e("ITEMS IN PLAYLIST AT Position 2:", "${it.items}")
           }
       }

*/
        //SearchTrackNetwork.searchTrack { searchResult ->
           // Log.e("Search RESULT Fragment:","${searchResult}")
        //}
    }
}