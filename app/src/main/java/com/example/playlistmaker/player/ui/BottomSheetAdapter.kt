package com.example.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.models.Playlist

class BottomSheetAdapter(private val items: List<Playlist>):
    RecyclerView.Adapter<BottomSheetViewHolder>() {
    private val itemList =  ArrayList(this.items)
    var clickListener: PlaylistClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        return BottomSheetViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val playlist = itemList[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener{clickListener?.onPlaylistClick(playlist)}
    }

    fun addItems(values: List<Playlist>) {
        this.itemList.clear()
        if (values.isNotEmpty()) {
            this.itemList.addAll(values)
        }
        this.notifyDataSetChanged()
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}