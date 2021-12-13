package com.example.hustlejams.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.CurrentSongsFromSearchAdapter
import com.example.hustlejams.databinding.FragmentCurrentSongsAddedFromSearchDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CurrentSongsAddedFromSearchDialogFragment: DialogFragment() {
    companion object {
        fun create(listener: () -> Unit): CurrentSongsAddedFromSearchDialogFragment {
            return CurrentSongsAddedFromSearchDialogFragment().apply {
                this.listener = listener
            }
        }
    }
    var currentSongsFromSearchAdapter:CurrentSongsFromSearchAdapter ?= null
    var listener:() -> Unit = {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentCurrentSongsAddedFromSearchDialogBinding.inflate(inflater)

        currentSongsFromSearchAdapter= CurrentSongsFromSearchAdapter {
            var indexAtMatchSong = 0
            var indexAtMatchTime = 0
            for((index, song) in Repository.listOfAddedSongsFromSearchObjectsRepo.withIndex()){
                if(it.id.toString() == song.id.toString() ){
                    indexAtMatchSong = index
                    for((indexTime, time) in Repository.timeForSongsAddedFromSearchList.withIndex()){
                        if(time == song.duration_ms){
                            indexAtMatchTime = indexTime
                        }
                    }
                }
            }
            Repository.timeForSongsAddedFromSearchList.removeAt(indexAtMatchTime)
            Repository.listOfAddedSongsFromSearchObjectsRepo.removeAt(indexAtMatchSong)
            currentSongsFromSearchAdapter?.submitList(Repository.listOfAddedSongsFromSearchObjectsRepo)

            //listener()
        }

        binding.apply {
            currentSongsFromSearchRecyclerView.apply {
                adapter = currentSongsFromSearchAdapter
                layoutManager = LinearLayoutManager(requireContext())
                currentSongsFromSearchAdapter!!.submitList(Repository.listOfAddedSongsFromSearchObjectsRepo)
            }
        }


        return MaterialAlertDialogBuilder(
            requireContext())
            .setView(binding.root)
            .setPositiveButton("Close"){_,_ ->
                listener()
            }
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        listener()
    }

}