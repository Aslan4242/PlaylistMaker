package com.example.playlistmaker.track

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val trackImage: ImageView = itemView.findViewById(R.id.trackImage)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(track: Track) {
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .transform(RoundedCorners(2))
            .placeholder(R.drawable.track_image_placeholder)
            .into(trackImage)
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
    }
}