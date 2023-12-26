package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track


class PlaylistTrackDbConvertor {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId ?: 0,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime= track.trackTimeMillis.toString(),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseYear = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
