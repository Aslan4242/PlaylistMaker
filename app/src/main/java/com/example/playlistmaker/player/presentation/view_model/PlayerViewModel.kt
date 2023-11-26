package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) :
    ViewModel() {

    private val _state = MutableLiveData<PlayerScreenState>()
    private var currentTime: String? = null
    private var playerTimerJob: Job? = null

    init {
        preparePlayer()
    }

    fun state(): LiveData<PlayerScreenState> = _state

    private fun setState(state: PlayerScreenState) {
        _state.setValue(state)
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl, {
            setState(PlayerScreenState.Prepared)
        }, {
            setState(PlayerScreenState.Paused(ZERO_TIME))
        })
    }

    fun playBackControl() {
        playerInteractor.playbackControl({ onStartPlayer() }, { onPausePlayer() })
    }

    private fun onStartPlayer() {
        setState(PlayerScreenState.Playing())
        updateProgressTime()
    }

    fun pausePlayer() {
        playerInteractor.pause { onPausePlayer() }
    }

    private fun onPausePlayer() {
        setState(PlayerScreenState.Paused(currentTime))
        playerTimerJob?.cancel()
    }

    private fun updateProgressTime() {
        playerTimerJob = viewModelScope.launch {
            while (
                _state.value is PlayerScreenState.Progress ||
                _state.value is PlayerScreenState.Playing
            ) {
                delay(UPDATE_PROGRESS_TIME_DELAY)
                currentTime = getCurrentTrackPosition()
                setState(PlayerScreenState.Progress(getCurrentTrackPosition()))
            }
        }
    }

    private fun getCurrentTrackPosition(): String? = playerInteractor.getCurrentPosition(ZERO_TIME)

    companion object {
        private const val ZERO_TIME = "00:00"
        private const val UPDATE_PROGRESS_TIME_DELAY = 300L
    }
}
