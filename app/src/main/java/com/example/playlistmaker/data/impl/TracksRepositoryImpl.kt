package com.example.playlistmaker.data.impl

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private lateinit var failMessage: String
    override fun search(
        expression: String,
        onSuccess: () -> Unit,
        onEmpty: () -> Unit,
        onFailure: () -> Unit
    ): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        failMessage = response.message
        if (response.resultCode == 200) {
            onSuccess()
            val trackList = ArrayList<Track>((response as TracksSearchResponse).results.map {
                Track(
                    it.trackName,
                    it.artistName,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.previewUrl,
                )
            })
            if (trackList.isEmpty()) {
                onEmpty()
            }
            return trackList
        } else {
            onFailure()
            return emptyList()
        }
    }

    override fun getMessage() = failMessage
}
