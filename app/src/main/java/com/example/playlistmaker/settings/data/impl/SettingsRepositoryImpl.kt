package com.example.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) : SettingsRepository {
    override fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_PREFS, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_PREFS, enabled)
            .apply()
    }

    companion object {
        private const val DARK_THEME_PREFS = "dark_theme_prefs"
    }
}