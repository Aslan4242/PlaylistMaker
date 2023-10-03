package com.example.playlistmaker.player.presentation.view_model

import android.os.Handler
import android.os.Looper
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewModel(private val track: Track, private val playerInteractor: PlayerInteractor) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlayerScreenState>()
    private var currentTime: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var currentPosition: String? = null

    init {
        preparePlayer()
    }

    fun observeState(): LiveData<PlayerScreenState> = stateLiveData

    private fun setState(state: PlayerScreenState) {
        stateLiveData.postValue(state)
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer(track.previewUrl, {
            setState(PlayerScreenState.Prepared)
        }, {
            setState(PlayerScreenState.Paused(ZERO_TIME))
        }
        )
    }

    fun playBackControl() {
        playerInteractor.playbackControl({ onStartPlayer() }, { onPausePlayer() })
    }

    private fun onStartPlayer() {
        setState(PlayerScreenState.Playing())
        handler.post(updateProgressTimeRunnable)
    }

    fun pausePlayer() {
        playerInteractor.pause { onPausePlayer() }
    }

    private fun onPausePlayer() {
        setState(PlayerScreenState.Paused(currentTime))
        stopTimerTask()
    }

    private fun stopTimerTask() {
        handler.removeCallbacks(updateProgressTimeRunnable)
    }

    private val updateProgressTimeRunnable = Runnable { updateProgressTime() }

    private fun updateProgressTime() {
        currentPosition = getCurrentTrackPosition()
        handler.postDelayed(updateProgressTimeRunnable, UPDATE_PROGRESS_TIME_DELAY)
        setState(PlayerScreenState.Progress(getCurrentTrackPosition()))
    }

    private fun getCurrentTrackPosition() : String? = playerInteractor.getCurrentPosition(ZERO_TIME)

    companion object {
        private const val ZERO_TIME = "00:00"
        private const val UPDATE_PROGRESS_TIME_DELAY = 200L
        fun factory(track: Track): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    PlayerViewModel(track, Creator.providePlayerInteractor())
                }
            }
        }
    }
}
