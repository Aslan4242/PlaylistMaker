package com.example.playlistmaker.player.presentation.mapper

import com.example.playlistmaker.player.presentation.models.ParcelableTrack
import com.example.playlistmaker.search.domain.models.Track

object ParcelableTrackMapper {

    fun map(track: Track): ParcelableTrack {
        return ParcelableTrack(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            albumName = track.collectionName,
            releaseYear = track.releaseDate,
            genreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }

    fun map(track: ParcelableTrack): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.albumName,
            releaseDate = track.releaseYear,
            primaryGenreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = track.isFavorite
        )
    }
}
