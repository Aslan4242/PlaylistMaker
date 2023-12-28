package com.example.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.models.Playlist

class PlaylistsAdapter(private val items: List<Playlist>) :
    RecyclerView.Adapter<PlaylistsViewHolder>() {
    private val itemList =  ArrayList(this.items)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(itemList[position])
    }

    fun addItems(values: List<Playlist>) {
        itemList.clear()
        if (values.isNotEmpty()) {
            itemList.addAll(values)
        }
        this.notifyDataSetChanged()
    }
}
