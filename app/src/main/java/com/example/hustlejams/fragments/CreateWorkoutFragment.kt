package com.example.hustlejams.fragments

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.room.RoomDatabase
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.database.WorkoutDatabase
import com.example.hustlejams.databinding.FragmentCreateWorkoutBinding
import com.example.hustlejams.fragments.dialogs.CreatePlayListDialogFragment
import com.example.hustlejams.networking.networkCalls.GetPlaylistSpecificNetwork
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class CreateWorkoutFragment: Fragment(R.layout.fragment_create_workout) {
    private var playListNameFromAddSongsToPlayListFragment =""
    var workoutNameTextInput = ""
    var workoutTimeTextInput = ""
    var workoutName = ""
    var workoutTime = ""
    var workoutDatabase:WorkoutDatabase ?=null
    private var workoutToStoreInDatabase: WorkoutClass?= null
    //var createWorkoutAdapter:
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCreateWorkoutBinding.bind(view)

        workoutDatabase = WorkoutDatabase.getInstance(requireContext())

        playListNameFromAddSongsToPlayListFragment = arguments?.getString("playlistWithAddedSongs")?:""
        workoutName = arguments?.getString("workoutName")?:""
        workoutTime = arguments?.getString("workoutTime")?:""
        if(playListNameFromAddSongsToPlayListFragment != "" && workoutName != "" && workoutTime != ""){
            binding.createPlaylistButton.isGone = true
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


                    GetPlaylistSpecificNetwork.getPlaylistSpecific {
                        //runBlocking {
                            //val job: Job = launch(IO){
                        Log.e("GET PLAYLIST SPECIFIC", "${it.name}")
                        val playlistObjectJsonString = convertTestClassToJsonString(it)
                        workoutToStoreInDatabase = WorkoutClass(
                            playlist_json_string = playlistObjectJsonString,
                            workout_duration = workoutTimeTextInput,
                            workout_name = workoutNameTextInput,
                            workout_type = "run"
                        )

                        val workoutToStoreInDatabaseString = convertWorkoutClassToJsonString(
                            workoutToStoreInDatabase!!
                        )
                       // addWorkoutToDatabase(workoutToStoreInDatabase!!)

                        if (workoutNameTextInput != "" && workoutTimeTextInput != "" && newlyCreatedPlaylistName.text != "") {
                            val fragmentMangaer = parentFragmentManager
                            val currentWorkoutFragment = CurrentWorkoutFragment()
                            val args = Bundle()
                            args.putString("workoutToStoreInDatabase",workoutToStoreInDatabaseString)
                            currentWorkoutFragment.arguments = args
                            fragmentMangaer.beginTransaction()
                                .replace(R.id.fragment_container_main, currentWorkoutFragment)
                                .addToBackStack("back")
                                .commit()
                        }
                                //joinAll()
                   // }

                    //}
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
    fun convertTestClassToJsonString(classObj:GetPlaylistSpecific):String{
        val gson = Gson()
        return gson.toJson(classObj)
    }
    fun convertWorkoutClassToJsonString(classObj:WorkoutClass):String{
        val gson = Gson()
        return gson.toJson(classObj)
    }
    suspend fun addWorkoutToDatabase(workout:WorkoutClass){
        workoutDatabase?.workoutDao()?.addWorkout(workout)
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