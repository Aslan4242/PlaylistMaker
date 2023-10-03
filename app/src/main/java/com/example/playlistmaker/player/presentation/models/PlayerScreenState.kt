package com.example.playlistmaker.player.presentation.models


sealed interface PlayerScreenState {
    object Prepared : PlayerScreenState

    data class Playing(val time: String? = null) : PlayerScreenState

    data class Progress(val time: String? = null) : PlayerScreenState

    data class Paused(val time: String? = null) : PlayerScreenState
}
