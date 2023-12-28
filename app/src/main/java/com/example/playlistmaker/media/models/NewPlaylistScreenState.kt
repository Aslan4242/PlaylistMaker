package com.example.playlistmaker.media.models


sealed interface NewPlaylistScreenState {
    object PlayListEmpty : NewPlaylistScreenState
    data class PlayListPartlyEmpty(val playlist: Playlist) : NewPlaylistScreenState
    data class PlayListIsNotEmpty(val playlist: Playlist) : NewPlaylistScreenState
}
