package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun saveFavoriteTrack(track: Track)

    suspend fun deleteFavoriteTrack(trackId: Long)

    suspend fun isFavorite(trackId: Long): Boolean
}