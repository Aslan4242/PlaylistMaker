package com.example.playlistmaker.player.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.presentation.models.PlayerPlaylistState
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayListTrackState
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val track: Track,
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoriteTracksInteractor,
    private val historyInteractor: HistoryInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private val _state = MutableLiveData<PlayerScreenState>()
    private var currentTime: String? = null
    private var playerTimerJob: Job? = null
    private var playlists: List<Playlist> = listOf()

    private val modeLiveData = MutableLiveData<PlayerPlaylistState>()
    fun observeMode(): LiveData<PlayerPlaylistState> = modeLiveData

    fun observeState(): LiveData<PlayerScreenState> = _state

    private val favoriteLiveData = MutableLiveData<Boolean>()
    fun observeFavoriteState(): LiveData<Boolean> = favoriteLiveData

    private val trackAddProcessStatus = SingleLiveEvent<PlayListTrackState>()
    fun getAddProcessStatus(): LiveData<PlayListTrackState> = trackAddProcessStatus

    init {
        preparePlayer()
        viewModelScope.launch {
            track.isFavorite = favoritesInteractor.isFavorite(track.trackId ?: 0)
            favoriteLiveData.value = track.isFavorite
        }
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    playlists = it
                }
        }
        setMode(PlayerPlaylistState.Player)
    }

    fun setMode(mode: PlayerPlaylistState) {
        modeLiveData.value = mode
    }

    private fun setAddProcessStatus(status: PlayListTrackState) {
        trackAddProcessStatus.value = status
    }

    private fun setState(state: PlayerScreenState) {
        _state.value = state
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

    fun clickOnAddToFavoriteButton() {
        viewModelScope.launch {
            track.isFavorite = !track.isFavorite
            favoriteLiveData.value = track.isFavorite
            if (track.isFavorite) {
                favoritesInteractor.saveFavoriteTrack(track)
            } else {
                track.trackId?.let { favoritesInteractor.deleteFavoriteTrack(it) }
            }
            historyInteractor.addTrackToSearchHistory(track)
        }
    }

    private fun getCurrentTrackPosition(): String? = playerInteractor.getCurrentPosition(ZERO_TIME)

    fun onNewPlaylistClick() {
        setMode(PlayerPlaylistState.NewPlaylist)
    }

    fun onPlayerAddTrackClick() {
        setMode(PlayerPlaylistState.BottomSheet(playlists))
    }

    fun addTrackToPlaylist(id: Long, playlistName: String?) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(track, id)
            setAddProcessStatus(PlayListTrackState.TrackIsAdded(playlistName))
        }
        setMode(PlayerPlaylistState.Player)
    }

    fun addTrackToPlaylist(playlist: Playlist) {
        if (playlist.id == null) {
            setMode(PlayerPlaylistState.Player)
        }
        if (playlist.trackList.contains(track.trackId)) {
            setAddProcessStatus(PlayListTrackState.TrackExist(playlist.name))
        } else {
            playlist.id?.let { addTrackToPlaylist(it, playlist.name) }
        }
    }

    companion object {
        private const val ZERO_TIME = "00:00"
        private const val UPDATE_PROGRESS_TIME_DELAY = 300L
    }
}
