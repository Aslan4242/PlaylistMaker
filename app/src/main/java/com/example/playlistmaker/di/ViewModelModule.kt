package com.example.playlistmaker.di

import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.presentation.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.media.presentation.view_model.NewPlaylistViewModel
import com.example.playlistmaker.media.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.media.presentation.view_model.PlaylistEditViewModel
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get(), get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track, get(), get(), get(), get())
    }

    viewModel {
        FavoritesViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        NewPlaylistViewModel(get())
    }

    viewModel { (playlistId: Long) ->
        PlaylistInfoViewModel(playlistId, get())
    }

    viewModel {(playlist: Playlist) ->
        PlaylistEditViewModel(playlist, get())
    }
}
