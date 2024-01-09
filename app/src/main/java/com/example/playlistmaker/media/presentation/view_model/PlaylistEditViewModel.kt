package com.example.playlistmaker.media.presentation.view_model


import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.models.Playlist

class PlaylistEditViewModel(
    playlist: Playlist,
    playlistInteractor: PlaylistInteractor,
) : NewPlaylistViewModel(playlistInteractor) {

    init {
        setPlaylistValue(playlist)
    }

    override fun needShowDialog() = false
}