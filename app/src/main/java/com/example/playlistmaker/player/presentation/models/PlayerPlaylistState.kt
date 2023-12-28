package com.example.playlistmaker.player.presentation.models

import com.example.playlistmaker.media.models.Playlist

sealed interface PlayerPlaylistState {
    object Player: PlayerPlaylistState
    object NewPlaylist: PlayerPlaylistState
    data class BottomSheet(val playlists: List<Playlist>) : PlayerPlaylistState
}