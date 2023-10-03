package com.example.playlistmaker.player.domain.api

interface PlayerRepository {
    fun release()
    fun preparePlayer(previewUrl: String?)
    fun setOnCompletionListener(onCompletion: () -> Unit)
    fun setOnPreparedListener(onPrepared: () -> Unit)
    fun start()
    fun pause()
    fun getCurrentPosition(): Int
}
