package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.MainActivity
import com.example.hustlejams.R
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.database.WorkoutDatabase
import com.example.hustlejams.databinding.FragmentCurrentWorkoutBinding
import com.google.gson.Gson
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CurrentWorkoutFragment: Fragment(R.layout.fragment_current_workout) {
    private val CLIENT_ID = "70280efd1f0c4d8291e7dccf08d22662"
    private val REDIRECT_URI = "hustlejams://callback"
    private var mSpotifyAppRemote: SpotifyAppRemote? = null
    private var connectionParams: ConnectionParams?= null
    private var binding:FragmentCurrentWorkoutBinding ?=null
    private var alreadyConnected = false
    private var workoutDatabase: WorkoutDatabase ?= null
    private var workoutList = mutableListOf<WorkoutClass>()


    private val REQUEST_CODE = 1337

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCurrentWorkoutBinding.bind(view)

       val workoutToStroreInDatabaseString = arguments?.getString("workoutToStoreInDatabase")
       val workoutToStroreInDabase = workoutToStroreInDatabaseString?.let {
           convertJsonStringToWorkoutClass(
               it
           )
       }

        workoutDatabase = WorkoutDatabase.getInstance(requireContext())
        CoroutineScope(IO).launch {
            if (workoutToStroreInDabase != null) {
                addWorkoutToDatabase(workoutToStroreInDabase)
            }
            workoutList = getWorkoutList().toMutableList()
            for(item in workoutList) {
                Log.e("ITEM INLIST OF WORKOUTS", "$item.")
            }
        }

        /*
        CoroutineScope(IO).launch {
           workoutList = getWorkoutList().toMutableList()
            for(item in workoutList) {
                Log.e("ITEM INLIST OF WORKOUTS", "$item.")
            }
        }

         */
        val activity = activity as MainActivity
        activity.playCurrentWorkoutPlaylist()

/*
        connectionParams = ConnectionParams.Builder(CLIENT_ID)
        .setRedirectUri(REDIRECT_URI)
        .showAuthView(true)
        .build()

 */

        }
    fun getWorkoutList():List<WorkoutClass>{
        val list =workoutDatabase?.workoutDao()?.getAllWorkouts()
        return list?: emptyList()
    }





    /*
    override fun onStart() {
        super.onStart()
        if(!alreadyConnected) {
            val connectionParams = ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build()
            SpotifyAppRemote.disconnect(mSpotifyAppRemote)
            SpotifyAppRemote.connect(requireContext(), connectionParams,
                object : ConnectionListener {
                    override fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote
                        Log.d("MainActivity", "Connected! Yay!")

                        // Now you can start interacting with App Remote
                        connected()
                    }

                    override fun onFailure(throwable: Throwable) {
                        Log.e("MyActivity", throwable.message, throwable)

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                })
        }else{
           mSpotifyAppRemote?.playerApi?.resume()
        }
    }

    private fun connected() {
        alreadyConnected = true
        // Play a playlist
        mSpotifyAppRemote!!.playerApi.play("spotify:playlist:${Repository.newlyCratedPlaylistId}")

        // Subscribe to PlayerState
        mSpotifyAppRemote!!.playerApi
            .subscribeToPlayerState()
            .setEventCallback { playerState: PlayerState ->
                val track = playerState.track
                if (track != null) {
                    Log.d("MainActivity", track.name + " by " + track.artist.name)
                }
            }
    }


    override fun onPause() {
        super.onPause()
       // mSpotifyAppRemote?.playerApi?.pause()
    }

     */
    fun addWorkoutToDatabase(workout:WorkoutClass){
        workoutDatabase?.workoutDao()?.addWorkout(workout)
    }

    fun convertJsonStringToWorkoutClass(stringObj:String):WorkoutClass{
        val gson = Gson()
        return gson.fromJson(stringObj,WorkoutClass::class.java)
    }


}