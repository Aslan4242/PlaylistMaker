package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.track.Track
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistory (private val sharedPreferences: SharedPreferences){
    private var trackListHistory = ArrayList<Track>()
    fun addToHistory(track: Track) {
        trackListHistory.remove(track)
            if (trackListHistory.size < 10) {
                trackListHistory.add(0, track)
            } else {
                trackListHistory.removeAt(9)
                trackListHistory.add(0, track)
            }
        saveToSharedPrefs()
    }
    fun clearHistory(){
        trackListHistory.clear()
        sharedPreferences.edit().remove(TRACK_HISTORY).apply()
    }
    fun getHistory():ArrayList<Track>{
        val jsonString = sharedPreferences.getString(TRACK_HISTORY,"")
        val json = GsonBuilder().create()
        val itemType = object : TypeToken<ArrayList<Track>>() {}.type
        trackListHistory = json.fromJson<ArrayList<Track>>(jsonString, itemType) ?: arrayListOf()
        return trackListHistory
    }

    private fun saveToSharedPrefs(){
        val json = Gson()
        val jsonString = json.toJson(trackListHistory)
        sharedPreferences
            .edit()
            .putString(TRACK_HISTORY, jsonString)
            .apply()
    }

    companion object {
        private const val TRACK_HISTORY = "track_history"
    }
}
