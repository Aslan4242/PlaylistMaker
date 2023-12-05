package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val historyRepository: HistoryRepository): HistoryInteractor {
    override fun getSearchHistory(): ArrayList<Track> {
        return historyRepository.getHistory()
    }

    override suspend fun addTrackToSearchHistory(track: Track) {
        historyRepository.getSearchHistory().collect { tracks ->
            tracks.removeIf { it.trackId == track.trackId }
            if (tracks.size < 10) {
                tracks.add(0, track)
            } else {
                tracks.removeAt(9)
                tracks.add(0, track)
            }
            historyRepository.saveToHistory(tracks)
        }
    }

    override suspend fun clearSearchHistory() {
        historyRepository.clearHistory()
    }
}
