package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface HistoryRepository {
    fun saveToSharedPrefs(trackList: ArrayList<Track>)

    fun clearHistory()

    fun getHistory() : ArrayList<Track>
}
