package com.example.playlistmaker.media.data.impl

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.media.data.converters.PlaylistTrackDbConvertor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbMapper: PlaylistDbConvertor,
    private val playlistTrackDbConvertor: PlaylistTrackDbConvertor
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return appDatabase.playlistDao().insertPlaylist(playlistDbMapper.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracksIdList  = playlist.trackList.toList()
        val entity = playlistDbMapper.map(playlist)
        appDatabase.playlistDao().deletePlaylist(entity)
        tracksIdList.forEach { trackId ->
            if (isUnusedPlaylistTrack(trackId)) {
                val track = appDatabase.playlistTrackDao().getPlaylistTrackById(trackId)
                appDatabase.playlistTrackDao().deletePlaylistTrack(track)
            }
        }
    }

    override suspend fun getPlaylistById(id: Long): Playlist? {
        val playlistEntity = appDatabase.playlistDao().getPlaylistById(id)
        return if (playlistEntity != null) {
            playlistDbMapper.map(playlistEntity)
        } else {
            null
        }
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return appDatabase.playlistDao().getPlaylists()
            .map { playlistEntity -> playlistDbMapper.map(playlistEntity) }
    }

    override suspend fun getFlowPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getFlowPlaylists().map { it.map { playlistEntity -> playlistDbMapper.map(playlistEntity) } }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        if (playlist != null) {
            appDatabase.playlistTrackDao().insertTrack(playlistTrackDbConvertor.map(track))
            if (track.trackId !== null) {
                playlist.trackList = playlist.trackList.plus(track.trackId)
            }

            playlist.trackCount += 1
            appDatabase.playlistDao().updatePlaylist(playlistDbMapper.map(playlist))
        }
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        if (playlist != null && track.trackId !== null) {
            playlist.trackList = playlist.trackList.minus(track.trackId)
            playlist.trackCount -= 1
            appDatabase.playlistDao().updatePlaylist(playlistDbMapper.map(playlist))
            val trackId = track.trackId ?: 0
            if (trackId > 0 && isUnusedPlaylistTrack(trackId)) {
                appDatabase.playlistTrackDao().deletePlaylistTrack(playlistTrackDbConvertor.map(track))
            }
        }
    }

    override suspend fun getFlowPlaylistById(id: Long): Flow<Playlist?> {
        val flowPlaylistEntity = appDatabase.playlistDao().getFlowPlaylistById(id)
        return (flowPlaylistEntity.map { playlistEntity -> if (playlistEntity != null) playlistDbMapper.map(playlistEntity) else null })
    }

    override suspend fun getPlaylistTracks(): List<Track> {
        return appDatabase.playlistTrackDao().getPlaylistTracks().map { playlistTrackEntity -> playlistTrackDbConvertor.map(playlistTrackEntity)}
    }

    override suspend fun getPlaylistTracksByTrackIdList(trackIdList: List<Long>): List<Track> {
        return if (trackIdList.isEmpty())
            listOf()
        else {
            val allPlaylistTracks = getPlaylistTracks()
            allPlaylistTracks.filter { trackIdList.indexOf(it.trackId) > -1 }
        }
    }

    private suspend fun isUnusedPlaylistTrack(trackId: Long): Boolean {
        val playlists = getPlaylists().filter { playlist -> playlist.trackList.indexOf(trackId) > -1 }
        return playlists.isEmpty()
    }
}
