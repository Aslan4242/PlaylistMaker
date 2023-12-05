package com.example.playlistmaker.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TrackEntity.TABLE_NAME)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = INTEGER)
    val id: Int? = null,
    val trackId: Long?,
    val artworkUrl100: String?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val releaseDate: Int?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long?,
    val previewUrl: String?,
) {
    companion object {
        const val TABLE_NAME = "track_table"
    }
}