package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    suspend fun saveToHistory(trackList: ArrayList<Track>)
    suspend fun getSearchHistory() : Flow<ArrayList<Track>>
    suspend fun clearHistory()
    fun getHistory() : ArrayList<Track>
}
