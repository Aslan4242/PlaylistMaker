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
    private val playlistTrackDbMapper: PlaylistTrackDbConvertor
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return appDatabase.playlistDao().insertPlaylist(playlistDbMapper.map(playlist))
    }

    override suspend fun getPlaylistById(id: Long): Playlist {
        return playlistDbMapper.map(appDatabase.playlistDao().getPlaylistById(id))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getPlaylists()
            .map { it.map { playlistEntity -> playlistDbMapper.map(playlistEntity) } }
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        val playlist = getPlaylistById(playlistId)
        appDatabase.playlistTrackDao().insertTrack(playlistTrackDbMapper.map(track))
        if (track.trackId !== null) {
            playlist.trackList = playlist.trackList.plus(track.trackId)
        }

        playlist.trackCount += 1
        appDatabase.playlistDao().updatePlaylist(playlistDbMapper.map(playlist))
    }
}
