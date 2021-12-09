package com.example.hustlejams.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hustlejams.MainActivity
import com.example.hustlejams.MainActivity.Companion.playCurrentWorkoutPlaylist
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.Repository.currentlyPlaying
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.database.WorkoutDatabase
import com.example.hustlejams.databinding.FragmentCurrentWorkoutBinding
import com.example.hustlejams.databinding.FragmentCurrentWorkoutFromStoredBinding
import com.example.hustlejams.networking.networkCalls.TrackDetailsNetwork
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CurrentWorkoutFragment: Fragment(R.layout.fragment_current_workout) {
    lateinit var binding:FragmentCurrentWorkoutBinding
    private var workoutDatabase: WorkoutDatabase ?= null
    var playlistSpecificClass:GetPlaylistSpecific ?=null


    var startTimeInMIlis:Int = 0
    val listOfTracksIdsInPlaylist = mutableListOf<String>()
    var pState: Subscription<PlayerState>?=null
    var listOfTrackNames = mutableListOf<String>()
    var track: Track?= null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCurrentWorkoutBinding.bind(view)
        Repository.lastFragment ="workoutFragment"

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
        }

        binding.currentWorkoutWorkoutName.text = workoutToStroreInDabase?.workout_name?:"not showing up name"

        Repository.lastWorkoutPlaying = workoutToStroreInDabase

        val playlistStringFromActualWorkoutFromStored = workoutToStroreInDabase?.playlist_json_string
        playlistStringFromActualWorkoutFromStored?.let {
            convertJsonStringToGetPlayListSpecificClass(
                it
            ){ playlistSpecific ->
                playlistSpecificClass = playlistSpecific

            }
        }
        Log.e("PLAYLISTID FROM STRING INSDE WORKOUT CLASS","${playlistSpecificClass?.id}")

        val listOfTracksFromPlaylistSpecificClass = playlistSpecificClass?.tracks?.items
        if (listOfTracksFromPlaylistSpecificClass != null) {
            for(track in listOfTracksFromPlaylistSpecificClass){
                listOfTracksIdsInPlaylist.add(track?.track?.id?:"")
            }
        }

        listOfTrackNames = mutableListOf()
        for(track in listOfTracksFromPlaylistSpecificClass!!){
            listOfTrackNames.add(track?.track?.name?:"")
        }

        getDurationFromFromNetworkUsingListOfIds {
            Log.e("START TIME IN MILIS","$it")


            val playlistImagesList = playlistSpecificClass?.images
            val selectedPlaylistImage = playlistImagesList?.get(0).toString()
            val modifiedSelectedPlaylistImage = removeFromImageUrl(selectedPlaylistImage)
            Log.e("PLAYLISY IMAGE",modifiedSelectedPlaylistImage)

            Picasso.get().load(modifiedSelectedPlaylistImage).into(binding.currentWorkoutBackgroundImagePlaylistImage)

            Repository.newlyCratedPlaylistId = playlistSpecificClass?.id.toString()
            Repository.listOfTrackNamesLastPlaying = listOfTrackNames


            //val activity = activity as MainActivity

            //activity.
            playCurrentWorkoutPlaylist(requireContext()) {
                currentlyPlaying = true
                Log.e("START PLAYING THIS TO SEE REOPENING APP A INSTALL","THIS")
                //Repository.mSpotify?.playerApi?.resume()
                startCountdownTimer(binding)

                playerStateStuff(binding){
                    //pState?.cancel()
                    Log.e("PLAYER STATE STUFF CALLBACK:","CALLED")
                }
            }
        }

    }

    fun addWorkoutToDatabase(workout:WorkoutClass){
        workoutDatabase?.workoutDao()?.addWorkout(workout)
    }

    fun convertJsonStringToWorkoutClass(stringObj:String):WorkoutClass{
        val gson = Gson()
        return gson.fromJson(stringObj,WorkoutClass::class.java)
    }

    fun playerStateStuff(binding: FragmentCurrentWorkoutBinding, callback:(Boolean) -> Unit){
        Handler(Looper.getMainLooper()).postDelayed({
            pState = Repository.mSpotify?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
                track = playerState.track
                if(listOfTrackNames.contains(track?.name)) {
                    binding.currentWorkoutNowPlayingTrack.text = track?.name.toString()
                    Picasso.get()
                        .load(removeFromTrackUriInSetEventCallback(track?.imageUri.toString()))
                        .into(binding.currentWorkoutBackgroundImagePlaylistImage)
                }else{
                    Log.e("ELSEEEEEE","PLAYER STUFF")
                    Repository.mSpotify!!.playerApi.pause()
                    Repository.mSpotify = null
                    currentlyPlaying = false
                    Repository.newlyCratedPlaylistId = ""
                    listOfTrackNames.clear()
                    listOfTracksIdsInPlaylist.clear()
                    startTimeInMIlis = 0
                    arguments?.clear()
                    callback(true)
                }
            }
        }, 500)//og working was 3500

    }

    fun convertJsonStringToGetPlayListSpecificClass(stringObj:String,callBack:(GetPlaylistSpecific) -> Unit){
        val gson = Gson()
        var theClass =  gson.fromJson(stringObj, GetPlaylistSpecific::class.java)
        callBack(theClass)
    }

    fun removeFromImageUrl(imageUrl:String):String{
        var urlString = imageUrl
        urlString = urlString.replace("url=","")
        urlString = urlString.replace(", width=640","")
        urlString = urlString.replace("Image(height=640, ","")
        urlString = urlString.replace(")","")
        return urlString
    }

    fun removeFromTrackUriInSetEventCallback(imageUri:String):String{
        var uri = imageUri
        uri = uri.replace("ImageId{spotify:image:","")
        uri = uri.replace("'}","")
        val completeUrl = "https://i.scdn.co/image/$uri"
        //Log.e("IMAGE FROM TRACK URI TO REAL URL:",completeUrl)
        return completeUrl
    }

    private fun startCountdownTimer(binding: FragmentCurrentWorkoutBinding?) {
        object : CountDownTimer(startTimeInMIlis.toLong(), 1000) {

            override fun onTick(millisUntilFinished: Long) {
                val minutes = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    millisUntilFinished
                                )
                            ))
                if (binding != null) {
                    binding.mTextViewCountDown.text = minutes
                }
            }

            override fun onFinish() {
                //Toast.makeText(requireContext(),"Workout has ended, as well as playlist", Toast.LENGTH_LONG).show()
            }
        }.start()
    }

    private fun getDurationFromFromNetworkUsingListOfIds(callback: (Int) -> Unit) {
        val listSize = listOfTracksIdsInPlaylist.size
        var counter = 0
        for (id in listOfTracksIdsInPlaylist) {
            ++counter
            Repository.trackIdForTime = id
            TrackDetailsNetwork.getTrackDetails {
                startTimeInMIlis += it.duration_ms ?: 0
                if(counter == listSize){
                    callback(startTimeInMIlis)
                }
            }
        }

    }
}