package com.example.hustlejams.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.hustlejams.R
import com.example.hustlejams.databinding.FragmentWorkoutsBinding

class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWorkoutsBinding.bind(view)

        binding.addWorkoutFAB.setOnClickListener {
            var fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction()
                .addToBackStack("back")
                .replace(R.id.fragment_container_main,CreateWorkoutFragment())
                .commit()

        }
    }
}