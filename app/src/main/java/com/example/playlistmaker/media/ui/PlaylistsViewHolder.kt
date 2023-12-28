package com.example.playlistmaker.media.ui

import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.presentation.utils.toastText
import java.io.File

class PlaylistsViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.media_playlist, parentView, false)
) {
    private val playlistImageView: ImageView by lazy { itemView.findViewById(R.id.mediaPlaylistImage)}
    private val playlistNameView: TextView by lazy { itemView.findViewById(R.id.mediaPlaylistName)}
    private val playlistTrackInfoView: TextView by lazy { itemView.findViewById(R.id.mediaPlaylistTrackCount)}

    fun bind(model: Playlist) {
        playlistNameView.text = model.name.toString()
        playlistTrackInfoView.text = toastText(model.trackCount)

        playlistImageView.setImageResource(R.drawable.track_image_placeholder_large)
        if (!model.filePath.isNullOrEmpty()) {
            val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myPlaylist")
            val file = model.filePath?.let { File(filePath, it) }
            playlistImageView.setImageURI(Uri.fromFile(file))
            playlistImageView.clipToOutline = true
        }

    }
}
