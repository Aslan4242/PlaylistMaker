package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search(
        expression: String,
    ): Flow<Resource<ArrayList<Track>>>
    fun getMessage(): String
}
