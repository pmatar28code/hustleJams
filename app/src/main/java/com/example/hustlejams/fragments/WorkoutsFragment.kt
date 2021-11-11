package com.example.hustlejams.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.R
import com.example.hustlejams.Repository
import com.example.hustlejams.databinding.FragmentWorkoutsBinding
import com.example.hustlejams.networking.networkCalls.GetUserNetwork

class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWorkoutsBinding.bind(view)

            GetUserNetwork.getUser {
                Repository.userId = it.id.toString()
                Log.e("User ID:", it.id.toString())
            }

        binding.addWorkoutFAB.setOnClickListener {
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction()
                .addToBackStack("back")
                .replace(R.id.fragment_container_main,CreateWorkoutFragment())
                .commit()
        }
    }
}