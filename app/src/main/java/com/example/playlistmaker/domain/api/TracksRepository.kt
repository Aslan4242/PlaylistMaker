package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun search(
        expression: String,
        onSuccess: () -> Unit,
        onEmpty: () -> Unit,
        onFailure: () -> Unit
    ): List<Track>
    fun getMessage(): String
}
