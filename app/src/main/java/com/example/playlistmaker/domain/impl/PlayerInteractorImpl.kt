package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.PlayerState

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    private var playerState = PlayerState.STATE_DEFAULT

    override fun release() {
        playerRepository.release()
    }

    override fun preparePlayer(
        previewUrl: String?,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit,
    ) {
        playerRepository.preparePlayer(previewUrl)
        playerRepository.setOnPreparedListener {
            onPreparedListener()
            playerState = PlayerState.STATE_PREPARED
        }
        playerRepository.setOnCompletionListener {
            onCompletionListener()
            playerState = PlayerState.STATE_PREPARED
        }
    }

    override fun start(onStart: () -> Unit) {
        playerRepository.start()
        playerState = PlayerState.STATE_PLAYING
        onStart()
    }

    override fun pause(onPause: () -> Unit) {
        playerRepository.pause()
        playerState = PlayerState.STATE_PAUSED
        onPause()
    }

    override fun playbackControl(onStart: () -> Unit, onPause: () -> Unit) {
        when (playerState) {
            PlayerState.STATE_PLAYING -> {
                pause(onPause)
            }
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                start(onStart)
            }
            else -> {}
        }
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }
}
