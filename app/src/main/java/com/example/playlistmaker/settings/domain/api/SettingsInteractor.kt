package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun getDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
}
