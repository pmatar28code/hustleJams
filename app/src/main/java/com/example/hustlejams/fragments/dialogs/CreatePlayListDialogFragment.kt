package com.example.hustlejams.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.hustlejams.Repository
import com.example.hustlejams.databinding.FragmentCreatePlaylistDialogBinding
import com.example.hustlejams.networking.networkCalls.CreatePlaylistNetwork

class CreatePlayListDialogFragment(): DialogFragment() {
    companion object {
        fun create(onAcceptListener: () -> Unit): CreatePlayListDialogFragment {
            return CreatePlayListDialogFragment().apply {
                this.onAcceptListener = onAcceptListener
            }
        }
    }
    private var onAcceptListener: () -> Unit = {}
    private var playlistName =""
    private var playlistDescription =""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val binding = FragmentCreatePlaylistDialogBinding.inflate(inflater)


        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setPositiveButton("Create") { _, _ ->
                binding.apply {
                    playlistName = dialogCreatePlaylistName.editText?.text.toString()
                    playlistDescription = dialogCreatePlaylistDescription.editText?.text.toString()
                    Repository.newPlaylistName = playlistName
                    Repository.newPlaylistDescription = playlistDescription
                    Repository.newlyCreatedPlaylistName = playlistName
                }
                if(playlistName != "" && playlistDescription != ""){
                    CreatePlaylistNetwork.createList {
                        Repository.newlyCratedPlaylistId = it.id.toString()
                        //Toast.makeText(parentFragment?.requireContext()," ${it.name} List Successfully created",Toast.LENGTH_SHORT).show()
                        onAcceptListener()
                    }

                }else{
                    //Toast.makeText(parentFragment?.requireContext(),"Please fill in name and description.",Toast.LENGTH_SHORT).show()
                }

            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}