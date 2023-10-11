package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private lateinit var failMessage: String
    override fun search(
        expression: String,
    ): Resource<ArrayList<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        failMessage = response.message
        if (response.resultCode == 200) {
            val trackList = ArrayList<Track>((response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.trackTimeMillis,
                    it.artworkUrl512,
                    it.previewUrl,
                )
            })
            if (trackList.isEmpty()) {
            }
            return Resource.Success(trackList)
        } else {
            return Resource.Error(getMessage())
        }
    }

    override fun getMessage() = failMessage
}