package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistRepository.addPlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        playlistRepository.addTrackToPlaylist(track, playlistId)
    }
}
