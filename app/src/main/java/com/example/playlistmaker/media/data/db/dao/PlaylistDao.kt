package com.example.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Delete
    suspend fun deletePlaylist(playlist: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlist_table ORDER BY id DESC")
    fun getFlowPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table")
    suspend fun getPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    fun getFlowPlaylistById(playlistId: Long): Flow<PlaylistEntity?>
}