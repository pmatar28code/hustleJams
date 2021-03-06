package com.example.hustlejams.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hustlejams.Repository
import com.example.hustlejams.database.WorkoutClass
import com.example.hustlejams.databinding.ItemWorkoutBinding
import com.squareup.picasso.Picasso

class WorkoutAdapter(
    var onCLick:(WorkoutClass) -> Unit,
    var deleteWorkoutOnCLick:(WorkoutClass) -> Unit
): ListAdapter<WorkoutClass, WorkoutAdapter.WorkoutViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<WorkoutClass>() {
            override fun areItemsTheSame(
                oldItem: WorkoutClass,
                newItem: WorkoutClass
            ): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: WorkoutClass,
                newItem: WorkoutClass
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWorkoutBinding.inflate(inflater, parent, false)
        return WorkoutViewHolder(binding,deleteWorkoutOnCLick)
    }
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener {
            onCLick(getItem(position))
        }

    }
    class WorkoutViewHolder(
        private val binding: ItemWorkoutBinding,
        private val deleteWorkoutOnCLick: (WorkoutClass) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(workout: WorkoutClass) {
            binding.apply {
                itemWorkoutName.text = workout.workout_name
                val playlistString = workout.playlist_json_string
                val playlist = Repository.convertJsonStringToGetPlayListSpecificClass(playlistString)
                Log.e("PLAYLIST IMAGE IN ADPATER","${playlist.images?.get(0)}")
                val playListImageCleanUrl = Repository.removeFromImageUrl(playlist.images?.get(0).toString())
                Picasso.get().load(playListImageCleanUrl).into(itemWorkoutImage)
                workoutsItemDeleteButton.setOnClickListener {
                    deleteWorkoutOnCLick(workout)
                }
            }
        }
    }
}