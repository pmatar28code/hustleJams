package com.example.hustlejams.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hustlejams.databinding.ItemCurrentSongsFromSearchBinding
import com.example.hustlejams.networking.networkClasses.SearchTrack

class CurrentSongsFromSearchAdapter(
    var onCLick:(SearchTrack.Tracks.Item) -> Unit
): ListAdapter<SearchTrack.Tracks.Item, CurrentSongsFromSearchAdapter.CurrentSongsFromSearchViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<SearchTrack.Tracks.Item>() {
            override fun areItemsTheSame(
                oldItem: SearchTrack.Tracks.Item,
                newItem: SearchTrack.Tracks.Item
            ): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: SearchTrack.Tracks.Item,
                newItem: SearchTrack.Tracks.Item
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentSongsFromSearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrentSongsFromSearchBinding.inflate(inflater, parent, false)
        return CurrentSongsFromSearchViewHolder(binding, onCLick)
    }
    override fun onBindViewHolder(holder: CurrentSongsFromSearchViewHolder, position: Int) {
        holder.onBind(getItem(position))
        //holder.itemView.setOnClickListener { onCLick(getItem(position)) }
        /*
        val item = getItem(position)
        holder.itemView.findViewById<ImageView>(R.id.add_image).setOnClickListener {
            Log.e("Clicked","${item.uri}")
            onCLick(item)
        }

         */
    }

    class CurrentSongsFromSearchViewHolder(
        private val binding: ItemCurrentSongsFromSearchBinding,
        private val onCLick: (SearchTrack.Tracks.Item) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(currentSong: SearchTrack.Tracks.Item) {
            binding.apply {
                songNameCurrentSong.text = currentSong.name
                deleteImageCurrentSong.setOnClickListener {
                    onCLick(currentSong)
                }
            }
        }
    }
}