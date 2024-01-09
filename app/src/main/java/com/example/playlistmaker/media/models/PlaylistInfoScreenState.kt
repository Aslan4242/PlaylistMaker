package com.example.playlistmaker.media.models


sealed interface PlaylistInfoScreenState {

    data class Error(val message: String) : PlaylistInfoScreenState

    data class Success(val data: Playlist) : PlaylistInfoScreenState
}