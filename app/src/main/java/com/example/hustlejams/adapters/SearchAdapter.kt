package com.example.hustlejams.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hustlejams.databinding.ItemSearchBinding
import com.example.hustlejams.networking.networkClasses.SearchTrack


class SearchAdapter(
    var onCLick:(SearchTrack.Tracks.Item) -> Unit
): ListAdapter<SearchTrack.Tracks.Item, SearchAdapter.SearchViewHolder>(diff) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding, onCLick)
    }
    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
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

    class SearchViewHolder(

        private val binding: ItemSearchBinding,
        private val onCLick: (SearchTrack.Tracks.Item) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(search:SearchTrack.Tracks.Item) {
            binding.apply {
                searchArtistName.text = search?.artists?.get(0)?.name
                searchAlbumName.text = search.album?.name
                searchTrackName.text = search.name
                searchTrackUri.text = search.uri

                addImage.setOnClickListener {
                    Log.e("Clicked","${search.uri}")
                    onCLick(search)
                }
            }
        }
    }
}

