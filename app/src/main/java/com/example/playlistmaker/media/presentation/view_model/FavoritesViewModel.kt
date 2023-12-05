package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.media.models.FavoriteTracksScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.SingleLiveEvent
import com.example.playlistmaker.search.presentation.utils.debounce
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteTracksInteractor: FavoriteTracksInteractor) : ViewModel(),
    DefaultLifecycleObserver {
    private val stateLiveData = MutableLiveData<FavoriteTracksScreenState>()
    fun observeState(): LiveData<FavoriteTracksScreenState> = stateLiveData

    private val showPlayer = SingleLiveEvent<Track>()

    private var isClickAllowed = true
    private val onTrackClickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = it
        }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        loadContent()
    }

    private fun renderState(state: FavoriteTracksScreenState) {
        stateLiveData.postValue(state)
    }

    private fun loadContent() {
        renderState(FavoriteTracksScreenState.Loading)
        viewModelScope.launch {
            favoriteTracksInteractor
                .getFavoriteTracks()
                .collect {
                    processResult(it)
                }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavoriteTracksScreenState.Empty)
        } else {
            renderState(FavoriteTracksScreenState.Content(tracks))
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            onTrackClickDebounce(true)
        }
        return current
    }

    fun showPlayer(track: Track) {
        if (clickDebounce()) {
            showPlayer.value = track
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}