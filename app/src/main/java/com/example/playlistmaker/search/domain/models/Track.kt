package com.example.playlistmaker.search.domain.models

data class Track(
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val collectionName: String?,
    val releaseDate: Int?,
    val primaryGenreName: String?,
    val country: String?,
    val trackTimeMillis: Long?,
    val artworkUrl100: String?,
    val previewUrl: String?,
    var isFavorite: Boolean = false
) {
    val artworkUrl512
        get() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}
