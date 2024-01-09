package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.models.PlaylistsScreenState
import com.example.playlistmaker.search.presentation.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel(), DefaultLifecycleObserver {
    private val stateLiveData = MutableLiveData<PlaylistsScreenState>()
    fun observeState(): LiveData<PlaylistsScreenState> = stateLiveData

    private val showPlaylistInfo = SingleLiveEvent<Long>()
    fun getShowPlaylistInfo(): LiveData<Long> = showPlaylistInfo

    init {
        viewModelScope.launch {
            playlistInteractor
                .getPlaylists()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            stateLiveData.postValue(PlaylistsScreenState.Empty)
        } else {
            stateLiveData.postValue(PlaylistsScreenState.Playlists(playlists))
        }
    }
}