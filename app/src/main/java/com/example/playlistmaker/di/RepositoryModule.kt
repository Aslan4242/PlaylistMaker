package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.impl.HistoryRepositoryImpl
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.HistoryRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(get(), get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get())
    }
}
