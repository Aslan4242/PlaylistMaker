package com.example.playlistmaker.media.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksScreenState {
    object Loading : FavoriteTracksScreenState
    object Empty : FavoriteTracksScreenState

    data class Content(val tracks: List<Track>) : FavoriteTracksScreenState
}