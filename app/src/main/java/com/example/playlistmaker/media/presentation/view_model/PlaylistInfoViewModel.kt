package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.models.PlaylistInfoScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.SingleLiveEvent
import com.example.playlistmaker.search.presentation.utils.debounce
import com.example.playlistmaker.search.presentation.utils.toastMinuteText
import com.example.playlistmaker.search.presentation.utils.toastTrackText
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistId: Long,
    private val playlistInteractor: PlaylistInteractor,
    ) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistInfoScreenState>()
    fun observeState(): LiveData<PlaylistInfoScreenState> = stateLiveData

    private val trackListLiveData = MutableLiveData<List<Track>>()
    fun observeTrackList(): LiveData<List<Track>> = trackListLiveData

    private val showPlayer = SingleLiveEvent<Track>()
    fun getShowPlayer(): LiveData<Track> = showPlayer

    private val showPlaylistEdit = SingleLiveEvent<Playlist>()
    fun getShowPlaylistEdit(): LiveData<Playlist> = showPlaylistEdit

    private val deletePlaylist = SingleLiveEvent<Boolean>()
    fun getDeletePlaylist(): LiveData<Boolean> = deletePlaylist

    private var isClickAllowed = true
    private val onTrackClickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = it
        }

    init {
        loadContent()
    }

    private fun loadContent() {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect { playlist ->
                    processResult(playlist)
                    if (playlist != null) {
                        setTrackList(playlistInteractor.getPlaylistTracksByTrackIdList(playlist.trackList))
                    }
                }
        }
    }

    private fun setState(state: PlaylistInfoScreenState) {
        stateLiveData.value = state
    }

    private fun setTrackList(trackList: List<Track>) {
        trackListLiveData.value = trackList
    }

    private fun processResult(playlist: Playlist?) {
        if (playlist == null) {
            setState(PlaylistInfoScreenState.Error(PLAYLIST_NOT_FOUND))
        } else {
            setState(PlaylistInfoScreenState.Success(playlist))
        }
    }

    private fun getTrackCount(): Int = trackListLiveData.value?.size ?: 0

    private fun getPlaylistTimeMillis(): Long = trackListLiveData.value?.sumOf { it.trackTimeMillis ?: 0 } ?: 0

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            onTrackClickDebounce(true)
        }
        return current
    }

    fun getTrackCountInfo(): String {
        val trackCount = getTrackCount()
        return toastTrackText(trackCount)
    }

    fun getPlaylistTimeInfo(): String {
        val minutes = getPlaylistTimeMillis() / 1000 / 60
        return toastMinuteText(minutes)
    }

    fun showPlayer(track: Track) {
        if (clickDebounce()) {
            showPlayer.value = track
        }
    }

    fun onDeleteTrackClick(track: Track) {
        if (stateLiveData.value is PlaylistInfoScreenState.Success) {
            viewModelScope.launch {
                val playlistId = (stateLiveData.value as PlaylistInfoScreenState.Success).data.id
                playlistInteractor.deleteTrackFromPlaylist(track, playlistId)
            }
        }
    }

    fun onSharePlaylist(): Boolean {
        return if (trackListLiveData.value.isNullOrEmpty() or (stateLiveData.value !is PlaylistInfoScreenState.Success))
            false
        else {
            val playlist = (stateLiveData.value as PlaylistInfoScreenState.Success).data
            val trackList = trackListLiveData.value.orEmpty()
            val playlistInfo = playlistInteractor.getPlaylistInfo(playlist, trackList)
            playlistInteractor.sharePlaylist(playlistInfo)
            true
        }
    }

    fun onDeletePlaylist() {
        if (stateLiveData.value is PlaylistInfoScreenState.Success) {
            viewModelScope.launch {

                val playlist = (stateLiveData.value as PlaylistInfoScreenState.Success).data
                playlistInteractor.deletePlaylist(playlist)
                deletePlaylist.value = true
            }
        }
    }

    fun showPlaylistEdit() {
        if (clickDebounce() && stateLiveData.value is PlaylistInfoScreenState.Success) {
            showPlaylistEdit.value = (stateLiveData.value as PlaylistInfoScreenState.Success).data
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val PLAYLIST_NOT_FOUND = "Плейлист не найден"
    }
}
