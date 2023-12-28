package com.example.playlistmaker.player.presentation.models

sealed interface PlayListTrackState {
    data class TrackIsAdded(val name: String?): PlayListTrackState
    data class TrackExist(val name: String?): PlayListTrackState
}
