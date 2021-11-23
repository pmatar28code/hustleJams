package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.WorkoutAdapter
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.database.WorkoutDatabase
import com.example.hustlejams.databinding.FragmentWorkoutsBinding
import com.example.hustlejams.networking.networkCalls.GetCurrentPlaylistsNetwork
import com.example.hustlejams.networking.networkCalls.GetUserNetwork
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {
    var workoutDatabase: WorkoutDatabase ?= null
    var workoutsList = mutableListOf<WorkoutClass>()
    var workoutAdapter:WorkoutAdapter ?= null
    var mapOfPlaylistsFromWorkoutDatabseClasses = mutableMapOf<Int,String>()
    var currentPlaylistIdssOnline = mutableListOf<String>()
    var listOfKeysNotFoundFromMap = mutableListOf<Int>()
    lateinit var binding:FragmentWorkoutsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWorkoutsBinding.bind(view)

            GetUserNetwork.getUser {
                Repository.userId = it.id.toString()
                Log.e("User ID:", it.id.toString())
            }

        workoutDatabase = WorkoutDatabase.getInstance(requireContext())

        CoroutineScope(IO).launch {
            workoutsList = getWorkoutsFromDatabase()?.toMutableList()
                ?: emptyList<WorkoutClass>().toMutableList()

            for(workout in workoutsList){
                val playlistString = workout.playlist_json_string
                val playlistSpecificClassObj = convertJsonStringToGetPlayListSpecificClass(playlistString)
                mapOfPlaylistsFromWorkoutDatabseClasses[workout.key] = playlistSpecificClassObj.id.toString()
            }

            for((k,v) in mapOfPlaylistsFromWorkoutDatabseClasses){
                 Log.e("WORKOUT KEY value TEST:", "key = $k , value = $v")
            }

            GetCurrentPlaylistsNetwork.getPlaylists {
                for (playlist in it.items?:emptyList()) {
                    currentPlaylistIdssOnline.add(playlist?.id!!)
                }
                Log.e("CURRENT PLAYLISTS IDS ONLINE", "$currentPlaylistIdssOnline")

                for ((k, v) in mapOfPlaylistsFromWorkoutDatabseClasses) {
                    if (!currentPlaylistIdssOnline.contains(v)) {
                        listOfKeysNotFoundFromMap.add(k)
                    }
                }
                Log.e("LISTS OF KEYS NOT FOUND:", "$listOfKeysNotFoundFromMap")

                if (listOfKeysNotFoundFromMap.isNotEmpty()){

                    CoroutineScope(IO).launch {
                        workoutsList.clear()
                        for (key in listOfKeysNotFoundFromMap) {

                            var testBool = workoutDatabase!!.workoutDao().removeWorkout(key)
                            Log.e("TEST BOOL:", "$testBool")
                        }
                        listOfKeysNotFoundFromMap.clear()
                        mapOfPlaylistsFromWorkoutDatabseClasses.clear()
                        currentPlaylistIdssOnline.clear()
                        workoutsList = getWorkoutsFromDatabase()?.toMutableList() ?: emptyList<WorkoutClass>().toMutableList()

                        setWorkoutAdapterAndRecyclerView(binding)
                    }
                }else {
                    CoroutineScope(IO).launch{
                    setWorkoutAdapterAndRecyclerView(binding)
                }
                }
            }
        }

        binding.addWorkoutFAB.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction()
                .addToBackStack("back")
                .replace(R.id.fragment_container_main,CreateWorkoutFragment())
                .commit()
        }
    }

    fun getWorkoutsFromDatabase(): List<WorkoutClass>? {
        return workoutDatabase?.workoutDao()?.getAllWorkouts()
    }

    fun convertWorkoutClassToJsonString(classObj: WorkoutClass):String{
        val gson = Gson()
        return gson.toJson(classObj)
    }

    fun convertJsonStringToGetPlayListSpecificClass(stringObj:String):GetPlaylistSpecific{
        val gson = Gson()
        return gson.fromJson(stringObj,GetPlaylistSpecific::class.java)
    }

    suspend fun test(binding:FragmentWorkoutsBinding){
        withContext(Dispatchers.Main) {
            binding.workoutsFragWorkoutRecyclerView.apply {
                adapter = workoutAdapter
                layoutManager = LinearLayoutManager(requireContext())
                workoutAdapter!!.submitList(workoutsList)
            }
        }
    }

    suspend fun setWorkoutAdapterAndRecyclerView(binding:FragmentWorkoutsBinding){
        workoutAdapter = WorkoutAdapter {
            val workoutClassString = convertWorkoutClassToJsonString(it)
            val fragmentManager = parentFragmentManager
            val currentWorkoutFromStored = CurrentWorkoutFromStored()
            val args = Bundle()
            args.putString("workout", workoutClassString)
            currentWorkoutFromStored.arguments = args
            fragmentManager.beginTransaction()
                .addToBackStack("back")
                .replace(R.id.fragment_container_main, currentWorkoutFromStored)
                .commit()
        }
        withContext(Dispatchers.Main) {
            binding.workoutsFragWorkoutRecyclerView.apply {
                adapter = workoutAdapter
                layoutManager = LinearLayoutManager(requireContext())
                workoutAdapter!!.submitList(workoutsList)
            }
        }
    }
}