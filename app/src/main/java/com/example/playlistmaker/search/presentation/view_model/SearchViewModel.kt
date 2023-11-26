package com.example.playlistmaker.search.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.HistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.models.SearchScreenState
import com.example.playlistmaker.search.presentation.utils.SingleEventLiveData
import com.example.playlistmaker.search.presentation.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val historyInteractor: HistoryInteractor
) : ViewModel() {
    private val _state = MutableLiveData<SearchScreenState>()
    fun state(): LiveData<SearchScreenState> = _state

    private val showPlayerTrigger = SingleEventLiveData<Track>()
    fun getShowPlayerTrigger(): LiveData<Track> = showPlayerTrigger

    private var lastSearchText: String? = null
    private var currentSearchText: String? = null

    private var isClickAllowed = true

    private val onSearchDebounce = debounce<String?>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { track ->
        if (track?.isNotEmpty() == true) {
            searchAction(track)
        }
    }

    private fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        onSearchDebounce(changedText)
    }

    fun onEditorAction() {
        searchAction(currentSearchText ?: "")
    }

    private fun searchAction(searchText: String) {
        if (searchText.isNotEmpty()) {
            setState(SearchScreenState.Progress)
            viewModelScope.launch {
                tracksInteractor.search(searchText).collect { pair ->
                    val tracks = ArrayList<Track>()
                    val collection = pair.first
                    if (collection != null) {
                        tracks.addAll(collection)
                    }
                    when {
                        pair.second != null -> {
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
            }
        }
    }

    private fun setState(state: SearchScreenState) {
        _state.postValue(state)
    }

    fun onEditTextChanged(hasFocus: Boolean, text: String?) {
        currentSearchText = text
        val tracks = getHistoryTrackList()
        if (hasFocus && currentSearchText?.isEmpty() == true && tracks.size > 0) {
            onSearchDebounce(currentSearchText)
            setState(SearchScreenState.History(tracks))
        } else {
            searchDebounce(currentSearchText ?: "")
            setState(
                SearchScreenState.List(
                    if (_state.value is SearchScreenState.List) {
                        (_state.value as SearchScreenState.List).tracks
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
            debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
                isClickAllowed = it
            }
        }
        return current
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
