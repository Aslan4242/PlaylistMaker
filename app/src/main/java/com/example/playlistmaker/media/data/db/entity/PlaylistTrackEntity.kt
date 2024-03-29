package com.example.playlistmaker.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_track_table")
data class PlaylistTrackEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(typeAffinity = INTEGER)
    val trackId: Long = 0,
    val trackName: String?,
    val artistName: String?,
    val trackTime: Long?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseYear: Int?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)
