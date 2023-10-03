package com.example.playlistmaker.player.presentation.mapper

import com.example.playlistmaker.player.presentation.models.ParcelableTrack
import com.example.playlistmaker.search.domain.models.Track

object ParcelableTrackMapper {

    fun map(track: Track): ParcelableTrack {
        return ParcelableTrack(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            albumName = track.collectionName,
            releaseDate = track.releaseDate,
            genreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(track: ParcelableTrack): Track {
        return Track(
            trackName = track.trackName.toString(),
            artistName = track.artistName.toString(),
            trackTimeMillis = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.albumName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.genreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}
