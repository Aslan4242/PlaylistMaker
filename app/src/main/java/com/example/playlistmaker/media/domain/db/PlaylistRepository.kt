package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist): Long

    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylistById(id: Long): Playlist?
    suspend fun getPlaylists(): List<Playlist>
    suspend fun addTrackToPlaylist(track: Track, playlistId: Long)
    suspend fun getFlowPlaylists(): Flow<List<Playlist>>
    suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long)
    suspend fun getFlowPlaylistById(id: Long): Flow<Playlist?>
    suspend fun getPlaylistTracks(): List<Track>
    suspend fun getPlaylistTracksByTrackIdList(trackIdList: List<Long>): List<Track>
}
