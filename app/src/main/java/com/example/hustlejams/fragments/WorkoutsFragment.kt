package com.example.hustlejams.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.hustlejams.R
import com.example.hustlejams.databinding.FragmentWorkoutsBinding

class WorkoutsFragment: Fragment(R.layout.fragment_workouts) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWorkoutsBinding.bind(view)
    }
}