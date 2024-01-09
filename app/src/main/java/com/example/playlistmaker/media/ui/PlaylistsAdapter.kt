package com.example.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.media.models.Playlist

class PlaylistsAdapter(private val items: List<Playlist>) : RecyclerView.Adapter<PlaylistsViewHolder>() {
    private val itemList =  ArrayList(this.items)
    private var onListElementClickListener: OnListElementClickListener? = null

    interface OnListElementClickListener {
        fun onListElementClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnListElementClickListener) {
        onListElementClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(itemList[position])
        holder.itemView.setOnClickListener {
            onListElementClickListener?.onListElementClick(position)
        }
    }

    fun addItems(values: List<Playlist>) {
        itemList.clear()
        if (values.isNotEmpty()) {
            itemList.addAll(values)
        }
        this.notifyDataSetChanged()
    }

    fun getPlayListId(position: Int): Long? {
        return itemList[position].id
    }
}
