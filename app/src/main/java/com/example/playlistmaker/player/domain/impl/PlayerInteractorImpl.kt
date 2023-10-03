package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.*

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

    override fun getCurrentPosition(zeroTime: String): String? {
        return when (playerState) {
            PlayerState.STATE_PLAYING -> SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                playerRepository.getCurrentPosition()
            )

            PlayerState.STATE_PREPARED -> zeroTime

            else -> null
        }
    }
}
