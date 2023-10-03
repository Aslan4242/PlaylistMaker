package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.Resource

interface TracksRepository {
    fun search(
        expression: String,
    ): Resource<ArrayList<Track>>
    fun getMessage(): String
}
