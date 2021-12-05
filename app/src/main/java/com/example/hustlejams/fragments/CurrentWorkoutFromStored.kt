package com.example.hustlejams.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import com.example.hustlejams.MainActivity
import com.example.hustlejams.MainActivity.Companion.playCurrentWorkoutPlaylist
import com.example.hustlejams.MainActivity.Companion.reAssignRepositoryAppRemote
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.Repository.currentlyPlaying
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.databinding.FragmentCurrentWorkoutFromStoredBinding
import com.example.hustlejams.networking.networkCalls.TrackDetailsNetwork
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.Subscription
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.squareup.picasso.Picasso
import java.util.concurrent.TimeUnit


class CurrentWorkoutFromStored: Fragment(R.layout.fragment_current_workout_from_stored) {

    var startTimeInMIlis:Int = 0
    val listOfTracksIdsInPlaylist = mutableListOf<String>()
    var pState: Subscription<PlayerState>?=null
    var listOfTrackNames = mutableListOf<String>()
    var track: Track ?= null
    lateinit var binding: FragmentCurrentWorkoutFromStoredBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCurrentWorkoutFromStoredBinding.bind(view)

        if(!currentlyPlaying) {
                    val actualWorkoutFromStoredString = arguments?.getString("workout")
                    val actualWorkoutFromStoredClass = actualWorkoutFromStoredString?.let {
                        convertJsonStringToWorkoutClass(
                            it
                        )
                    }
                    binding.currentWorkoutFromStoredWorkoutName.text =
                        actualWorkoutFromStoredClass?.workout_name ?: "not showing up name"

                    val playlistStringFromActualWorkoutFromStored =
                        actualWorkoutFromStoredClass?.playlist_json_string
                    val playlistSpecificClass = playlistStringFromActualWorkoutFromStored?.let {
                        convertJsonStringToGetPlayListSpecificClass(
                            it
                        )
                    }
                    Log.e(
                        "PLAYLISTID FROM STRING INSDE WORKOUT CLASS",
                        "${playlistSpecificClass?.id}"
                    )

                    val listOfTracksFromPlaylistSpecificClass = playlistSpecificClass?.tracks?.items
                    if (listOfTracksFromPlaylistSpecificClass != null) {
                        for (track in listOfTracksFromPlaylistSpecificClass) {
                            listOfTracksIdsInPlaylist.add(track?.track?.id ?: "")
                        }
                    }

                    listOfTrackNames = mutableListOf()
                    for (track in listOfTracksFromPlaylistSpecificClass!!) {
                        listOfTrackNames.add(track?.track?.name ?: "")
                    }

                    getDurationFromFromNetworkUsingListOfIds {
                        Log.e("START TIME IN MILIS", "$it")


                        val playlistImagesList = playlistSpecificClass.images
                        val selectedPlaylistImage = playlistImagesList?.get(0).toString()
                        val modifiedSelectedPlaylistImage =
                            removeFromImageUrl(selectedPlaylistImage)
                        Log.e("PLAYLISY IMAGE", modifiedSelectedPlaylistImage)

                        Picasso.get().load(modifiedSelectedPlaylistImage)
                            .into(binding.backgroundImagePlaylistImage)

                        Repository.newlyCratedPlaylistId = playlistSpecificClass.id.toString()
                        Repository.listOfTrackNamesLastPlaying = listOfTrackNames

                        //val activity = activity as MainActivity

                        //activity.
                        playCurrentWorkoutPlaylist(requireContext()) {
                            currentlyPlaying = true
                            binding.backgroundImagePlaylistImage.setOnClickListener {
                                stopWorkoutButtonFunction()
                            }
                            Log.e("START PLAYING THIS TO SEE REOPENING APP A INSTALL", "THIS")
                            // Repository.mSpotify?.playerApi?.resume()
                            startCountdownTimer(binding)

                            playerStateStuff(binding) {
                                //pState?.cancel()
                                Log.e("PLAYER STATE STUFF CALLBACK:", "CALLED")
                            }
                        }
                    }
        }else {
            reAssignRepositoryAppRemote()
            listOfTrackNames.clear()
            listOfTrackNames = Repository.listOfTrackNamesLastPlaying
            binding.backgroundImagePlaylistImage.setOnClickListener {
                stopWorkoutButtonFunction()
            }
            playerStateStuff(binding) {}
        }





    }

    fun stopWorkoutButtonFunction(){
        if(Repository.mSpotify != null) {
            Log.e("stop button", "this")
            Repository.mSpotify!!.playerApi.pause()
            Repository.mSpotify = null
            currentlyPlaying = false
            Repository.newlyCratedPlaylistId = ""
            listOfTrackNames.clear()
            listOfTracksIdsInPlaylist.clear()
            startTimeInMIlis = 0
            arguments?.clear()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, WorkoutsFragment())
                .commit()
        }else{
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_main, WorkoutsFragment())
                .commit()
        }
    }


    fun playerStateStuff(binding:FragmentCurrentWorkoutFromStoredBinding,callback:(Boolean) -> Unit){
        Handler(Looper.getMainLooper()).postDelayed({
            pState = Repository.mSpotify?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
                track = playerState.track
                if(listOfTrackNames.contains(track?.name)) {
                    binding.currentWorkoutFromStoredNowPlayingTrack.text = track?.name.toString()
                    Picasso.get()
                        .load(removeFromTrackUriInSetEventCallback(track?.imageUri.toString()))
                        .into(binding.backgroundImagePlaylistImage)
                }else{
                    currentlyPlaying = false
                    binding.currentWorkoutStoredStopImageButton.setImageResource(R.drawable.ic_back)
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
        }, 500)

    }

    fun convertJsonStringToWorkoutClass(stringObj:String):WorkoutClass{
        val gson = Gson()
        return gson.fromJson(stringObj,WorkoutClass::class.java)
    }

    fun convertJsonStringToGetPlayListSpecificClass(stringObj:String):GetPlaylistSpecific{
        val gson = Gson()
        return gson.fromJson(stringObj,GetPlaylistSpecific::class.java)
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

    private fun startCountdownTimer(binding: FragmentCurrentWorkoutFromStoredBinding) {
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
            binding.mTextViewCountDown.text = minutes
        }

        override fun onFinish() {
//           Toast.makeText(requireContext(),"Workout has ended, as well as playlist",Toast.LENGTH_LONG).show()
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