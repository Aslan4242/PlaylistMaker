package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {
    override fun search(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.search(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }
}
