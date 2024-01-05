package com.example.playlistmaker.media.data.converters

import com.example.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.models.Track


class PlaylistTrackDbConvertor {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId ?: 0,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime= track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseYear = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(playlistTrack: PlaylistTrackEntity): Track {
        return Track(
            trackId = playlistTrack.trackId,
            trackName = playlistTrack.trackName,
            artistName = playlistTrack.artistName,
            trackTimeMillis= playlistTrack.trackTime,
            artworkUrl100 = playlistTrack.artworkUrl100,
            collectionName = playlistTrack.collectionName,
            releaseDate = playlistTrack.releaseYear,
            primaryGenreName = playlistTrack.primaryGenreName,
            country = playlistTrack.country,
            previewUrl = playlistTrack.previewUrl
        )
    }
}
