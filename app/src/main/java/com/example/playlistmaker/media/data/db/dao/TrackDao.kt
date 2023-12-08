package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Query("SELECT * FROM ${TrackEntity.TABLE_NAME}")
    suspend fun getTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM ${TrackEntity.TABLE_NAME} ")
    suspend fun getTracksId(): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(track: TrackEntity)

    @Query("DELETE FROM ${TrackEntity.TABLE_NAME}  WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)
}