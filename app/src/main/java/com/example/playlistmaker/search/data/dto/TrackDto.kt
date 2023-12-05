package com.example.playlistmaker.search.data.dto

import java.util.Date

class TrackDto(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val collectionName: String,
    val releaseDate: Date,
    val primaryGenreName: String,
    val country: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val previewUrl: String,
)
