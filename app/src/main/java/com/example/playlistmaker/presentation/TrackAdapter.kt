package com.example.playlistmaker.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track

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

    override fun getItemCount() = tracks.size

    fun getTrack(position: Int): Track {
        return tracks[position]
    }
}
