package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.db.FavoritesTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoritesTracksRepository
): FavoriteTracksInteractor {
    override suspend fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksRepository.getTracks()
    }

    override suspend fun saveFavoriteTrack(track: Track) {
        favoriteTracksRepository.addTrack(track)
    }

    override suspend fun deleteFavoriteTrack(trackId: Long) {
        favoriteTracksRepository.deleteTrack(trackId)
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return favoriteTracksRepository.isFavorite(trackId)
    }
}