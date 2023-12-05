package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.TrackEntity
import com.example.playlistmaker.search.domain.models.Track

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            artistName = track.artistName,
            trackName = track.trackName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            artistName = track.artistName,
            trackName = track.trackName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            isFavorite = true
        )
    }
}