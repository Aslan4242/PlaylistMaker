package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            filePath = playlist.filePath,
            trackList = createJsonFromTrackIdList(playlist.trackList.toTypedArray()),
            trackCount = playlist.trackCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            filePath = playlistEntity.filePath,
            trackList = ArrayList(createTrackIdListFromJson(playlistEntity.trackList)),
            trackCount = playlistEntity.trackCount
        )
    }

    private fun createTrackIdListFromJson(json: String?): ArrayList<Long> {
        return if (json.isNullOrEmpty()) {
            ArrayList()
        } else {
            val typeOfTrackIdList: Type = object : TypeToken<ArrayList<Long?>?>() {}.type
            return gson.fromJson(json, typeOfTrackIdList)
        }
    }

    private fun createJsonFromTrackIdList(trackIdList: Array<Long>): String {
        return gson.toJson(trackIdList)
    }
}