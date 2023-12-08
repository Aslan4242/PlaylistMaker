package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.data.converters.TrackDbConvertor
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.db.FavoritesTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor,
): FavoritesTracksRepository {
    override suspend fun addTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().addTrack(trackEntity)
    }

    override fun getTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks().reversed()
        emit(convertFromTrackEntity(tracks))
    }

    override suspend fun deleteTrack(trackId: Long) {
        appDatabase.trackDao().deleteTrack(trackId)
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().getTracksId().contains(trackId)
    }
    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}