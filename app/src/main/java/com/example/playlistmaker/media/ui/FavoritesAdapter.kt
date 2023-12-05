package com.example.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class FavoritesAdapter(private var tracks: ArrayList<Track>) : RecyclerView.Adapter<FavoritesViewHolder>() {
    private var onListElementClickListener: OnListElementClickListener? = null


    interface OnListElementClickListener {
        fun onListElementClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnListElementClickListener) {
        onListElementClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onListElementClickListener?.onListElementClick(position)
        }
    }

    fun addItems(values: List<Track>) {
        this.tracks.clear()
        if (values.isNotEmpty()) {
            this.tracks.addAll(values)
        }
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = tracks.size

    fun getTrack(position: Int): Track {
        return tracks[position]
    }
}