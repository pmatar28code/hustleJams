package com.example.hustlejams.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.databinding.FragmentCreateWorkoutBinding
import com.example.hustlejams.fragments.dialogs.CreatePlayListDialogFragment

class CreateWorkoutFragment: Fragment(R.layout.fragment_create_workout) {
    private var playListNameFromAddSongsToPlayListFragment =""
    var workoutNameTextInput = ""
    var workoutTimeTextInput = ""
    var workoutName = ""
    var workoutTime = ""
    //var createWorkoutAdapter:
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateWorkoutBinding.bind(view)

        playListNameFromAddSongsToPlayListFragment = arguments?.getString("playlistWithAddedSongs")?:""
        workoutName = arguments?.getString("workoutName")?:""
        workoutTime = arguments?.getString("workoutTime")?:""
        if(playListNameFromAddSongsToPlayListFragment != "" && workoutName != "" && workoutTime != ""){
            binding.newlyCreatedPlaylistName.text = playListNameFromAddSongsToPlayListFragment
            binding.nameTextInputLayout.editText?.setText(workoutName)
            binding.timeTextInputLayout.editText?.setText(workoutTime)
        }else{
            binding.newlyCreatedPlaylistName.text = ""
            binding.nameTextInputLayout.editText?.setText("")
            binding.timeTextInputLayout.editText?.setText("")

        }


        binding.apply {
            addSongsToPlaylistButton.isGone = true
            createPlaylistButton.setOnClickListener {
                workoutNameTextInput = nameTextInputLayout.editText?.text.toString()
                workoutTimeTextInput = timeTextInputLayout.editText?.text.toString()

                if(workoutNameTextInput != "" && workoutTimeTextInput !="") {

                    CreatePlayListDialogFragment.create {
                        Toast.makeText(
                            requireContext(),
                            " ${Repository.newPlaylistName} List Successfully created",
                            Toast.LENGTH_SHORT
                        ).show()
                        addSongsToPlaylistButton.isVisible = true
                        createPlaylistButton.isGone = true
                        newlyCreatedPlaylistTag.isGone = true
                        newlyCreatedPlaylistName.text = Repository.newlyCreatedPlaylistName
                        //submit list
                    }.show(parentFragmentManager, "open create playlist dialog")
                }else{
                    Toast.makeText(requireContext(),"Please fill in name and time duration",Toast.LENGTH_LONG).show()
                }


            }

            addSongsToPlaylistButton.setOnClickListener {
                val timeTextInput = timeTextInputLayout.editText?.text.toString()
                if(timeTextInput != "") {
                    Repository.workoutTime = timeTextInputLayout.editText?.text.toString().toInt()
                }

                val fragManager = parentFragmentManager
                val addSongsToPlaylistFragment = AddSongsToPlayListFragment()
                val args =  Bundle()
                args.putString("name", Repository.newlyCreatedPlaylistName)
                args.putString("playlistId",Repository.newlyCratedPlaylistId)
                args.putString("workoutName",workoutNameTextInput)
                args.putString("workoutTime",workoutTimeTextInput)
                addSongsToPlaylistFragment.arguments = args
                fragManager.beginTransaction()
                    .replace(R.id.fragment_container_main,addSongsToPlaylistFragment)
                    .addToBackStack("back")
                    .commit()
            }

            createWorkoutButton.setOnClickListener {
                workoutNameTextInput = nameTextInputLayout.editText?.text.toString()
                workoutTimeTextInput = timeTextInputLayout.editText?.text.toString()
                if(workoutNameTextInput != "" && workoutTimeTextInput != "" && newlyCreatedPlaylistName.text != ""){
                    //create workout
                }
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


/*
//Put the value
YourNewFragment ldf = new YourNewFragment ();
Bundle args = new Bundle();
args.putString("YourKey", "YourValue");
ldf.setArguments(args);

//Inflate the fragment
getFragmentManager().beginTransaction().add(R.id.container, ldf).commit();
In onCreateView of the new Fragment:

//Retrieve the value
String value = getArguments().getString("YourKey");
 */