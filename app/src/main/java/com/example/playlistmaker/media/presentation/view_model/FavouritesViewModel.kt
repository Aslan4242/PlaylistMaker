package com.example.playlistmaker.media.presentation.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.media.models.FavouriteTracksScreenState

class FavouritesViewModel() : ViewModel() {
    private val stateLiveData = MutableLiveData<FavouriteTracksScreenState>()
    fun observeState(): LiveData<FavouriteTracksScreenState> = stateLiveData

    init {
        setState(FavouriteTracksScreenState.Empty)
    }

    private fun setState(state: FavouriteTracksScreenState) {
        stateLiveData.postValue(state)
    }
}