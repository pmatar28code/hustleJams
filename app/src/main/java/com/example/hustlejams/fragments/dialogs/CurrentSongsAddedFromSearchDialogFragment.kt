package com.example.hustlejams.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hustlejams.Repository
import com.example.hustlejams.adapters.CurrentSongsFromSearchAdapter
import com.example.hustlejams.databinding.FragmentCurrentSongsAddedFromSearchDialogBinding
import com.example.hustlejams.networking.networkClasses.SearchTrack
import com.example.hustlejams.viewModels.addSongsFragViewModel
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
    var list : List<SearchTrack.Tracks.Item> ?= null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentCurrentSongsAddedFromSearchDialogBinding.inflate(inflater)

        //val addSongsViewModel = ViewModelProvider(
            //this).get(addSongsFragViewModel::class.java)

        val addSongsViewModel: addSongsFragViewModel by viewModels()
        addSongsViewModel.getCurrentSongsLiveData()
        val liveDataSongsList = addSongsViewModel.currentSongsLiveData

        liveDataSongsList.observe(this, {
            list = it
            Log.e("IS IT UPDATING IT?","$list")
            //currentSongsFromSearchAdapter?.submitList(it)
            binding.apply {
                currentSongsFromSearchRecyclerView.apply {
                    adapter = currentSongsFromSearchAdapter
                    layoutManager = LinearLayoutManager(requireContext())
                    currentSongsFromSearchAdapter!!.submitList(list)
                }
            }
        })

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
            updateLiveDataSongsList()
            //listener()
        }

        binding.apply {
            currentSongsFromSearchRecyclerView.apply {
                adapter = currentSongsFromSearchAdapter
                layoutManager = LinearLayoutManager(requireContext())
                currentSongsFromSearchAdapter!!.submitList(list)
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

    fun updateLiveDataSongsList(){
        val addSongViewModel:addSongsFragViewModel by viewModels()
        addSongViewModel.getCurrentSongsLiveData()
    }
    override fun onDestroy() {
        super.onDestroy()
        listener()
    }

}