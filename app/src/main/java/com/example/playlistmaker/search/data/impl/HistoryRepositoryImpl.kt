package com.example.playlistmaker.search.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences) : HistoryRepository {

    override fun saveToSharedPrefs(trackList: ArrayList<Track>) {
        val json = Gson()
        val jsonString = json.toJson(trackList)
        sharedPreferences
            .edit()
            .putString(TRACK_HISTORY, jsonString)
            .apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(TRACK_HISTORY).apply()
    }

    override fun getHistory(): ArrayList<Track> {
        val jsonString = sharedPreferences.getString(TRACK_HISTORY, "")
        val json = GsonBuilder().create()
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        return json.fromJson<ArrayList<Track>>(jsonString, itemType) ?: arrayListOf()
    }

    companion object {
        private const val TRACK_HISTORY = "track_history"
    }
}
