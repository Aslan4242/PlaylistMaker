package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) : HistoryRepository {

    override suspend fun saveToHistory(trackList: ArrayList<Track>) {
        val json = Gson()
        val jsonString = json.toJson(trackList)
        sharedPreferences
            .edit()
            .putString(TRACK_HISTORY, jsonString)
            .apply()
    }

    override suspend fun clearHistory() {
        sharedPreferences.edit().remove(TRACK_HISTORY).apply()
    }

    override fun getHistory(): ArrayList<Track> {
        return convertFromString()
    }

    override suspend fun getSearchHistory(): Flow<ArrayList<Track>> = flow {
        emit(convertFromString())
    }

    private fun convertFromString(): ArrayList<Track> {
        val jsonString = sharedPreferences.getString(TRACK_HISTORY, "")
        val itemType = object : TypeToken<ArrayList<Track?>?>() {}.type
        return gson.fromJson(jsonString, itemType) ?: arrayListOf()
    }

    companion object {
        private const val TRACK_HISTORY = "track_history"
    }
}
