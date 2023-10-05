package com.example.playlistmaker.search.presentation.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import com.example.playlistmaker.search.presentation.utils.SingleEventLiveData

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SearchViewModel(
                    Creator.provideTracksInteractor(),
                    Creator.provideHistoryInteractorImpl(application)
                )
            }
        }
    }

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    fun observeState(): LiveData<SearchScreenState> = stateLiveData

    private val showPlayerTrigger = SingleEventLiveData<Track>()
    fun getShowPlayerTrigger(): LiveData<Track> = showPlayerTrigger

    private var lastSearchText: String? = null
    private var currentSearchText: String? = null

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchAction(newSearchText)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(searchRunnable)
    }

    private fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun onEditorAction() {
        searchAction(currentSearchText ?: "")
    }

    private fun searchAction(searchText: String) {
        if (searchText.isNotEmpty()) {
            setState(SearchScreenState.Progress)
            tracksInteractor.search(searchText, object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: ArrayList<Track>?, errorMessage: String?) {
                    val tracks = ArrayList<Track>()

                    if (foundTracks != null) {
                        tracks.addAll(foundTracks)
                    }

                    when {
                        errorMessage != null -> {
                            setState(SearchScreenState.Error)
                        }

                        tracks.isEmpty() -> {
                            setState(SearchScreenState.Empty)
                        }

                        else -> {
                            setState(SearchScreenState.List(tracks = tracks))
                        }
                    }
                }
            })
        }
    }

    private fun setState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    fun onEditTextChanged(hasFocus: Boolean, text: String?) {
        currentSearchText = text
        val tracks = getHistoryTrackList()
        if (hasFocus && currentSearchText?.isEmpty() == true && tracks.size > 0) {
            handler.removeCallbacks(searchRunnable)
            setState(SearchScreenState.History(tracks))
        } else {
            searchDebounce(currentSearchText ?: "")
            setState(
                SearchScreenState.List(
                    if (stateLiveData.value is SearchScreenState.List) {
                        (stateLiveData.value as SearchScreenState.List).tracks
                    } else {
                        ArrayList()
                    }
                )
            )
        }
    }

    fun onEditFocusChange(hasFocus: Boolean) {
        val tracks = getHistoryTrackList()
        if (hasFocus && currentSearchText.isNullOrEmpty() && tracks.size > 0) {
            setState(SearchScreenState.History(tracks))
        } else {
            setState(SearchScreenState.History(tracks))
        }
    }


    private fun getHistoryTrackList(): ArrayList<Track> {
        return historyInteractor.getSearchHistory()
    }

    fun addTrackToSearchHistory(track: Track) {
        historyInteractor.addTrackToSearchHistory(track)
    }

    fun onClearSearchHistoryButtonClick() {
        historyInteractor.clearSearchHistory()
        setState(SearchScreenState.List(ArrayList()))
    }

    fun showPlayer(track: Track) {
        if (clickDebounce()) {
            showPlayerTrigger.value = track
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
}
