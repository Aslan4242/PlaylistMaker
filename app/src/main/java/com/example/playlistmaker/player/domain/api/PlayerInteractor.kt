package com.example.playlistmaker.player.domain.api


interface PlayerInteractor {
    fun release()
    fun preparePlayer(
        previewUrl: String?,
        onPreparedListener: () -> Unit,
        onCompletionListener: () -> Unit
    )
    fun start(onStart: () -> Unit)
    fun pause(onPause: () -> Unit)
    fun playbackControl(onStart: () -> Unit, onPause: () -> Unit)
    fun getCurrentPosition(zeroTime: String): String?
}
