package com.example.playlistmaker.media.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(typeAffinity = INTEGER)
    val id: Long,
    val name: String?,
    val description: String?,
    val filePath: String?,
    val trackList: String?,
    val trackCount: Int = 0,
)
