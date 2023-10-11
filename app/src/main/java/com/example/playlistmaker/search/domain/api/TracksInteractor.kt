package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {
    fun search(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?)
    }
}