package com.example.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(var tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder> () {

    private var onListElementClickListener: OnListElementClickListener? = null

    interface OnListElementClickListener {
        fun onListElementClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnListElementClickListener) {
        onListElementClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            onListElementClickListener?.onListElementClick(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateTrackList(values: ArrayList<Track>) {
        this.tracks.clear()
        if (values.size > 0) {
            this.tracks.addAll(values)
        }
        this.notifyDataSetChanged()
    }

    override fun getItemCount() = tracks.size

    fun getTrack(position: Int): Track {
        return tracks[position]
    }
}
