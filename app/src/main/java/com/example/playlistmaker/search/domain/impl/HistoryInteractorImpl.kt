package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class HistoryInteractorImpl(private val historyRepository: HistoryRepository): HistoryInteractor {
    override fun getSearchHistory(): ArrayList<Track> {
        return historyRepository.getHistory()
    }

    override fun addTrackToSearchHistory(track: Track) {
        val trackListHistory = getSearchHistory()

        trackListHistory.remove(track)
        if (trackListHistory.size < 10) {
            trackListHistory.add(0, track)
        } else {
            trackListHistory.removeAt(9)
            trackListHistory.add(0, track)
        }
        historyRepository.saveToSharedPrefs(trackListHistory)
    }

    override fun clearSearchHistory() {
        historyRepository.clearHistory()
    }
}
