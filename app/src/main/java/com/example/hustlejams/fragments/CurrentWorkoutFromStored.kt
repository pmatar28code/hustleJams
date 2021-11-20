package com.example.hustlejams.fragments

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.MainActivity
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.databinding.FragmentCurrentWorkoutFromStoredBinding
import com.example.hustlejams.networking.networkCalls.CurrentlyPlayingTrackNetwork
import com.example.hustlejams.networking.networkCalls.GetCurrentPlaylistsNetwork
import com.example.hustlejams.networking.networkClasses.CurrentlyPlayingTrack
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.PlayerState
import com.spotify.protocol.types.Track
import com.squareup.picasso.Picasso
import java.util.*

class CurrentWorkoutFromStored: Fragment(R.layout.fragment_current_workout_from_stored) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentCurrentWorkoutFromStoredBinding.bind(view)

        val actualWorkoutFromStoredString = arguments?.getString("workout")
        val actualWorkoutFromStoredClass = actualWorkoutFromStoredString?.let {
            convertJsonStringToWorkoutClass(
                it
            )
        }
        binding.currentWorkoutFromStoredWorkoutName.text = actualWorkoutFromStoredClass?.workout_name?:"not showing up name"

        val playlistStringFromActualWorkoutFromStored = actualWorkoutFromStoredClass?.playlist_json_string
        val playlistSpecificClass = playlistStringFromActualWorkoutFromStored?.let {
            convertJsonStringToGetPlayListSpecificClass(
                it
            )
        }
        Log.e("PLAYLISTID FROM STRING INSDE WORKOUT CLASS","${playlistSpecificClass?.id}")

        val playlistImagesList = playlistSpecificClass?.images
        val selectedPlaylistImage = playlistImagesList?.get(0).toString()
        val modifiedSelectedPlaylistImage = removeFromImageUrl(selectedPlaylistImage)
        Log.e("PLAYLISY IMAGE",modifiedSelectedPlaylistImage)

        Picasso.get().load(modifiedSelectedPlaylistImage).into(binding.backgroundImagePlaylistImage)

        if (playlistSpecificClass != null) {
            Repository.newlyCratedPlaylistId = playlistSpecificClass.id.toString()
        }

        val activity = activity as MainActivity



        var playListSize = playlistSpecificClass?.tracks?.items?.size

        var counter =0



        activity.playCurrentWorkoutPlaylist {
            Repository.mSpotify?.playerApi?.subscribeToPlayerState()?.setEventCallback { playerState ->
                val track: Track? = playerState.track
                binding.currentWorkoutNowPlayingTrack.text = track?.name.toString()
                //Log.e("TRACK IMAGE URI:","${track?.imageUri}")
                Picasso.get().load(removeFromTrackUriInSetEventCallback(track?.imageUri.toString())).into(binding.backgroundImagePlaylistImage)

                
            }

            //Repository.mSpotify?.playerApi?.subscribeToPlayerState()?.setEventCallback {
               // Log.e("EVENT CALLBACK PLAYER STATE,", it.track.name)
               // binding.currentWorkoutNowPlayingTrack.text = it.track.name.toString()

            //}
        }

/*
        activity.playCurrentWorkoutPlaylist(){
            CurrentlyPlayingTrackNetwork.getCurrentlyPlayingTrack { currentTrack ->
                binding.currentWorkoutNowPlayingTrack.text = currentTrack.item?.name

                Repository.mSpotify?.playerApi?.subscribeToPlayerState()?.setEventCallback {
                    val hasEnded = trackWasStarted && isPaused && position == 0L
                    if(currentTrack.item?.name != it.track.name){
                        binding.currentWorkoutFromStoredWorkoutName.text = it.track.name
                    }
                }

            /*
                var songDuration = it.item?.durationMs?.toLong()
                var t = (3).toLong()
                //var timer = Timer()
                //timer.schedule(TimerTask(),songDuration)
                val delay = 1000L
                repeatDelayed(delay) {
                    if (songDuration != null) {
                        repeatDelayed(t){
                            Log.e("REPEAT DeLAY:","SONG ENDED $songDuration")
                        }
                    }

                 */
                //}

                //when time reached run code again this using some kind system time counter
                //for the counter to call a fun at specific time. that  could be this same code
                //++counter for while.
                //maybe change image to track image here also?
            }


        }
        */


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
        Log.e("IMAGE FROM TRACK URI TO REAL URL:",completeUrl)
        return completeUrl
    }
/*
    fun repeatDelayed(delay: Long, todo: () -> Unit) {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                todo()
                handler.postDelayed(this, delay)
            }
        }, delay)
    }

 */
}