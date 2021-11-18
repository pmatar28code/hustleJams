package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.MainActivity
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.databinding.FragmentCurrentWorkoutFromStoredBinding
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson

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
        binding.curentWorkoutFromStoredWorkoutName.text = actualWorkoutFromStoredClass?.workout_name?:"not showing up name"

        val playlistStringFromActualWorkoutFromStored = actualWorkoutFromStoredClass?.playlist_json_string
        val playlistSpecificClass = playlistStringFromActualWorkoutFromStored?.let {
            convertJsonStringToGetPlayListSpecificClass(
                it
            )
        }
        Log.e("PLAYLISTID FROM STRING INSDE WORKOUT CLASS","${playlistSpecificClass?.id}")

        if (playlistSpecificClass != null) {
            Repository.newlyCratedPlaylistId = playlistSpecificClass.id.toString()
        }

        var activity = activity as MainActivity
        activity.playCurrentWorkoutPlaylist()

    }
    fun convertJsonStringToWorkoutClass(stringObj:String):WorkoutClass{
        val gson = Gson()
        return gson.fromJson(stringObj,WorkoutClass::class.java)
    }

    fun convertJsonStringToGetPlayListSpecificClass(stringObj:String):GetPlaylistSpecific{
        val gson = Gson()
        return gson.fromJson(stringObj,GetPlaylistSpecific::class.java)
    }
}