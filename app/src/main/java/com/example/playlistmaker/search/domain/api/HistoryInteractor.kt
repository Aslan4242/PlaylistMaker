package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun getSearchHistory() : ArrayList<Track>

    fun addTrackToSearchHistory(track: Track)

    fun clearSearchHistory()
}
