package com.example.playlistmaker.media.domain.db

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesTracksRepository {

    suspend fun addTrack(track: Track)
    suspend fun deleteTrack(trackId: Long)
    suspend fun getTracks(): Flow<List<Track>>
    suspend fun isFavorite(trackId: Long): Boolean
}