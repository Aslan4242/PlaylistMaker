package com.example.playlistmaker.search.presentation.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface SearchScreenState {

    object Progress : SearchScreenState

    object Empty : SearchScreenState

    object Error : SearchScreenState

    data class List(
        val tracks: ArrayList<Track>
    ) : SearchScreenState

    data class History(
        val tracks: ArrayList<Track>
    ) : SearchScreenState
}
