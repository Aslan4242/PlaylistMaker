package com.example.playlistmaker.media.models

data class Playlist(
    var id: Long = 0,
    var name: String? = null,
    var description: String? = null,
    var filePath: String? = null,
    var trackList: List<Long> = ArrayList(),
    var trackCount: Int = 0,
)
