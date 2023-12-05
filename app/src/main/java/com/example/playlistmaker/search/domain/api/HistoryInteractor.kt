package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryInteractor {
    fun getSearchHistory() : ArrayList<Track>

    suspend fun addTrackToSearchHistory(track: Track)

    suspend fun clearSearchHistory()
}
