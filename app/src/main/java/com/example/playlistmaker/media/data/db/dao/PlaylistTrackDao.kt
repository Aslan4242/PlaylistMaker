package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Delete
    suspend fun deletePlaylistTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_track_table")
    suspend fun getPlaylistTracks(): List<PlaylistTrackEntity>

    @Query("SELECT * FROM playlist_track_table WHERE trackId = :trackId")
    suspend fun getPlaylistTrackById(trackId: Long): PlaylistTrackEntity

}
