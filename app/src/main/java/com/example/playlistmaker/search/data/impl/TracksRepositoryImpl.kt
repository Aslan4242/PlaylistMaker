package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {
    private lateinit var failMessage: String
    override fun search(
        expression: String,
    ): Flow<Resource<ArrayList<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        failMessage = response.message
        if (response.resultCode == 200) {
            val favoriteTracks = appDatabase.trackDao().getTracksId()
            val trackList = ArrayList<Track>((response as TracksSearchResponse).results.map {
                Track(
                    trackId = it.trackId,
                    trackName = it.trackName,
                    artistName = it.artistName,
                    collectionName = it.collectionName,
                    releaseDate = getReleaseDate(it.releaseDate),
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    trackTimeMillis = it.trackTimeMillis,
                    artworkUrl100 = it.artworkUrl100,
                    previewUrl = it.previewUrl,
                    isFavorite = favoriteTracks.contains(it.trackId)
                )
            })
            emit(Resource.Success(trackList))
        } else {
            emit(Resource.Error(getMessage()))
        }
    }

    private fun getReleaseDate(date: Date?) =
        (date?.let { SimpleDateFormat("yyyy", Locale.getDefault()).format(it).orEmpty() })?.toInt()

    override fun getMessage() = failMessage
}
