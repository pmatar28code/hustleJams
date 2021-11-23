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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class CurrentWorkoutFragment: Fragment(R.layout.fragment_current_workout) {
    private var binding:FragmentCurrentWorkoutBinding ?=null
    private var workoutDatabase: WorkoutDatabase ?= null
    private var workoutList = mutableListOf<WorkoutClass>()

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

        val activity = activity as MainActivity
        activity.playCurrentWorkoutPlaylist(){

        }
    }

    fun getWorkoutList():List<WorkoutClass>{
        val list =workoutDatabase?.workoutDao()?.getAllWorkouts()
        return list?: emptyList()
    }

    fun addWorkoutToDatabase(workout:WorkoutClass){
        workoutDatabase?.workoutDao()?.addWorkout(workout)
    }

    fun convertJsonStringToWorkoutClass(stringObj:String):WorkoutClass{
        val gson = Gson()
        return gson.fromJson(stringObj,WorkoutClass::class.java)
    }


}