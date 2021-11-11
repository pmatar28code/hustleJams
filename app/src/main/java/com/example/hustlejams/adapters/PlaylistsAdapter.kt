package com.example.hustlejams.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hustlejams.databinding.ItemPlaylistsBinding
import com.example.hustlejams.networking.networkClasses.GetPlaylists

class PlaylistsAdapter(
    var onCLick:(GetPlaylists.Item) -> Unit
): ListAdapter<GetPlaylists.Item, PlaylistsAdapter.PlaylistsViewHolder>(diff) {
    companion object {
        val diff = object : DiffUtil.ItemCallback<GetPlaylists.Item>() {
            override fun areItemsTheSame(
                oldItem: GetPlaylists.Item,
                newItem: GetPlaylists.Item
            ): Boolean {
                return oldItem == newItem
            }
            override fun areContentsTheSame(
                oldItem: GetPlaylists.Item,
                newItem: GetPlaylists.Item
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlaylistsBinding.inflate(inflater, parent, false)
        return PlaylistsViewHolder(binding)
    }
    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener { onCLick(getItem(position)) }

    }
    class PlaylistsViewHolder(

        private val binding: ItemPlaylistsBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(playlist: GetPlaylists.Item) {
            binding.apply {
                playlistName.text = playlist.name
                playlistSongsCount.text = playlist.tracks?.total.toString()
            }
        }
    }
}