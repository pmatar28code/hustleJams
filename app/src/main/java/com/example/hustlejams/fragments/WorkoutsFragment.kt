package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.WorkoutAdapter
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.database.WorkoutDatabase
import com.example.hustlejams.databinding.FragmentWorkoutsBinding
import com.example.hustlejams.networking.networkCalls.GetCurrentPlaylistsNetwork
import com.example.hustlejams.networking.networkCalls.GetUserNetwork
import com.example.hustlejams.networking.networkClasses.GetPlaylistSpecific
import com.example.hustlejams.networking.networkClasses.GetPlaylists
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO

class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {
    var workoutDatabase: WorkoutDatabase ?= null
    var workoutsList = mutableListOf<WorkoutClass>()
    var workoutAdapter:WorkoutAdapter ?= null
    var playlistsFromDatabaseIds = mutableListOf<String>()
    var playlistsFromWorkoutDatabaseClases = mutableListOf<GetPlaylistSpecific>()
    var mapOfPlaylistsFromWorkoutDatabseClasses = mutableMapOf<Int,String>()
    var currentPlaylistIdssOnline = mutableListOf<String>()
    var playlistIdsNotOnline = mutableListOf<String>()
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
               // playlistsFromWorkoutDatabaseClases.add(playlistSpecificClassObj)
                mapOfPlaylistsFromWorkoutDatabseClasses[workout.key] = playlistSpecificClassObj.id.toString()
            }
            //workoutsList.clear()

            for((k,v) in mapOfPlaylistsFromWorkoutDatabseClasses){
                 Log.e("WORKOUT KEY value TEST:", "key = $k , value = $v")
            }

            //for(playlist in playlistsFromWorkoutDatabaseClases){
              //  playlistsFromDatabaseIds.add(playlist.id!!)
           // }

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

                //for(key in listOfKeysNotFoundFromMap){
                //  workoutDatabase?.workoutDao()?.removeWorkout(key)
                //}

                //workoutsList.clear()

                //workoutsList = getWorkoutsFromDatabase()?.toMutableList()?:emptyList<WorkoutClass>().toMutableList()
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
                        // workoutDatabase!!.workoutDao().deleteAllWorkouts()
                        workoutsList = getWorkoutsFromDatabase()?.toMutableList() ?: emptyList<WorkoutClass>().toMutableList()
                        //test(binding)



                        setWorkoutAdapterAndRecyclerView(binding)



                    }
                }else {
                    CoroutineScope(IO).launch{
                    setWorkoutAdapterAndRecyclerView(binding)
                }
                }
            }


            //workoutsList = getWorkoutsFromDatabase()?.toMutableList()?:emptyList<WorkoutClass>().toMutableList()




        }

        binding.addWorkoutFAB.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction()
                .addToBackStack("back")
                .replace(R.id.fragment_container_main,CreateWorkoutFragment())
                .commit()
        }
    }

    suspend fun deleteWorkoutFromDatabase(key:Int){
        workoutDatabase!!.workoutDao().removeWorkout(key)
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
/*
    override fun onResume() {
        super.onResume()
        workoutsList.clear()
        performAllSteps(binding)
        workoutAdapter?.submitList(workoutsList)
    }


    override fun onStop() {
        super.onStop()
        workoutsList.clear()
        //performAllSteps(binding)
        workoutAdapter?.submitList(workoutsList)
    }

 */




    fun performAllSteps(binding:FragmentWorkoutsBinding) {
        CoroutineScope(IO).launch {
            workoutsList = getWorkoutsFromDatabase()?.toMutableList()
                ?: emptyList<WorkoutClass>().toMutableList()

            for(workout in workoutsList){
                val playlistString = workout.playlist_json_string
                val playlistSpecificClassObj = convertJsonStringToGetPlayListSpecificClass(playlistString)
                // playlistsFromWorkoutDatabaseClases.add(playlistSpecificClassObj)
                mapOfPlaylistsFromWorkoutDatabseClasses[workout.key] = playlistSpecificClassObj.id.toString()
            }
            //workoutsList.clear()

            for((k,v) in mapOfPlaylistsFromWorkoutDatabseClasses){
                // Log.e("WORKOUT KEY value TEST:", "key = $k , value = $v")
            }

            //for(playlist in playlistsFromWorkoutDatabaseClases){
            //  playlistsFromDatabaseIds.add(playlist.id!!)
            // }

            GetCurrentPlaylistsNetwork.getPlaylists {
                for (playlist in it.items!!) {
                    currentPlaylistIdssOnline.add(playlist?.id!!)
                }
                Log.e("CURRENT PLAYLISTS IDS ONLINE", "$currentPlaylistIdssOnline")

                for ((k, v) in mapOfPlaylistsFromWorkoutDatabseClasses) {
                    if (!currentPlaylistIdssOnline.contains(v)) {
                        listOfKeysNotFoundFromMap.add(k)
                    }
                }
                Log.e("LISTS OF KEYS NOT FOUND:", "$listOfKeysNotFoundFromMap")

                //for(key in listOfKeysNotFoundFromMap){
                //  workoutDatabase?.workoutDao()?.removeWorkout(key)
                //}

                //workoutsList.clear()

                //workoutsList = getWorkoutsFromDatabase()?.toMutableList()?:emptyList<WorkoutClass>().toMutableList()
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
                        // workoutDatabase!!.workoutDao().deleteAllWorkouts()
                        workoutsList = getWorkoutsFromDatabase()?.toMutableList()
                            ?: emptyList<WorkoutClass>().toMutableList()

                        test(binding)
                    }
                }

            }


            //workoutsList = getWorkoutsFromDatabase()?.toMutableList()?:emptyList<WorkoutClass>().toMutableList()



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