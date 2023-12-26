package com.example.playlistmaker.media.models

sealed interface NewPlayListState {
    object SaveError : NewPlayListState
    data class SaveSuccess(
        val playlist: Playlist
    ) : NewPlayListState
}