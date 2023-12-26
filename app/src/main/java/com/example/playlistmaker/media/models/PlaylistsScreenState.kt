package com.example.playlistmaker.media.models

sealed interface PlaylistsScreenState {
    object Empty : PlaylistsScreenState
    object Loading : PlaylistsScreenState
    data class Playlists(val playlists: List<Playlist>) : PlaylistsScreenState
}