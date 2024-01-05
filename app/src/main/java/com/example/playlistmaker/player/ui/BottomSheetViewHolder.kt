package com.example.playlistmaker.player.ui

import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.presentation.utils.toastTrackText
import java.io.File

class BottomSheetViewHolder(parentView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parentView.context)
        .inflate(R.layout.player_playlist, parentView, false)
) {
    private val playlistImageView: ImageView by lazy { itemView.findViewById(R.id.playerPlaylistImage)}
    private val playlistNameView: TextView by lazy { itemView.findViewById(R.id.playerPlaylistName)}
    private val playlistTrackInfoView: TextView by lazy { itemView.findViewById(R.id.playerPlaylistTrackCount)}

    fun bind(model: Playlist) {
        playlistNameView.text = model.name.toString()
        playlistTrackInfoView.text = toastTrackText(model.trackCount)

        playlistImageView.setImageResource(R.drawable.track_image_placeholder)
        if (!model.filePath.isNullOrEmpty()) {
            val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myPlaylist")
            val file = model.filePath?.let { File(filePath, it) }
            playlistImageView.setImageURI(Uri.fromFile(file))
            playlistImageView.clipToOutline = true
        }
    }
}