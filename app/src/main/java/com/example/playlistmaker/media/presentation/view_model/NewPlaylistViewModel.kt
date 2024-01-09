package com.example.playlistmaker.media.presentation.view_model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.models.NewPlaylistScreenState
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.models.NewPlayListState
import com.example.playlistmaker.search.presentation.utils.SingleLiveEvent
import com.example.playlistmaker.search.presentation.utils.debounce
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

open class NewPlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {
    private val playlist: Playlist = Playlist()
    private val stateLiveData = MutableLiveData<NewPlaylistScreenState>()
    fun observeState(): LiveData<NewPlaylistScreenState> = stateLiveData
    private val resultLiveData = SingleLiveEvent<NewPlayListState>()
    fun resultState(): LiveData<NewPlayListState> = resultLiveData
    private val addPlaylist = SingleLiveEvent<Playlist>()
    fun getAddPlaylist(): LiveData<Playlist> = addPlaylist

    private var isClickAllowed = true
    private val onPlaylistClickDebounce =
        debounce<Boolean>(CLICK_DEBOUNCE_DELAY, viewModelScope, false) {
            isClickAllowed = it
        }

    init {
        setState(NewPlaylistScreenState.PlayListEmpty)
    }

    fun setPlayListImage() {
        playlist.filePath = "playlist_${UUID.randomUUID()}.jpg"
        setCurrentState()
    }

    private fun setState(state: NewPlaylistScreenState) {
        stateLiveData.postValue(state)
    }

    private fun setResult(result: NewPlayListState) {
        resultLiveData.postValue(result)
    }

    private fun setCurrentState() {
        when {
            !playlist.name.isNullOrEmpty() -> setState(
                NewPlaylistScreenState.PlayListIsNotEmpty(playlist)
            )

            !playlist.description.isNullOrEmpty() || !playlist.filePath.isNullOrEmpty() -> {
                setState(
                    NewPlaylistScreenState.PlayListPartlyEmpty(playlist)
                )
            }

            else -> {
                setState(NewPlaylistScreenState.PlayListEmpty)
            }
        }
    }

    fun setPlayListImage(inputStream: InputStream?, dir: File?) {
        val file = playlist.filePath?.let {
            File(dir, it)
        }
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
    }

    fun addPlaylist(aPlaylist: Playlist) {
        viewModelScope.launch {
            try {
                val res = playlistInteractor.addPlaylist(playlist = aPlaylist)
                playlist.id = res
                setResult(NewPlayListState.SaveSuccess(playlist))
            } catch (e: Exception) {
                setResult(NewPlayListState.SaveError)
            }
        }
    }

    fun onPlaylistNameChanged(text: String?) {
        playlist.name = text
        setCurrentState()
    }

    fun onPlaylistDescriptionChanged(text: String?) {
        playlist.description = text
        setCurrentState()
    }

    open fun needShowDialog(): Boolean {
        return stateLiveData.value is NewPlaylistScreenState.PlayListIsNotEmpty || stateLiveData.value is NewPlaylistScreenState.PlayListPartlyEmpty
    }

    fun onCancelPlaylist() {
        setResult(NewPlayListState.SaveError)
    }

    fun onAddPlaylistClick() {
        if (clickDebounce()) {
            addPlaylist.value = playlist
        }
    }

    protected fun setPlaylistValue(value: Playlist) {
        playlist.id = value.id
        playlist.name = value.name
        playlist.description = value.description
        playlist.filePath = value.filePath
        playlist.trackList = value.trackList
        playlist.trackCount = value.trackCount
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            onPlaylistClickDebounce(true)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val QUALITY = 30
    }
}