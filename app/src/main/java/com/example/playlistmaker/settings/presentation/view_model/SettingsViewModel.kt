package com.example.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
    application: Application
) : AndroidViewModel(application) {

    private var darkTheme = false
    private val appthemeStateLiveData = MutableLiveData(darkTheme)
    fun observeAppTheme(): LiveData<Boolean> = appthemeStateLiveData
    init {
        darkTheme = settingsInteractor.getDarkTheme()
        appthemeStateLiveData.postValue(darkTheme)
    }

    fun onSwitchThemeBtnClick(isChecked: Boolean) {
        appthemeStateLiveData.postValue(isChecked)
        settingsInteractor.setDarkTheme(isChecked)
        switchTheme(isChecked)
    }
    fun onShareAppBtnClick() {
        sharingInteractor.shareApp()
    }

    fun onSupportBtnClick() {
        sharingInteractor.openSupport()
    }

    fun onTermsOfUseBtnClick() {
        sharingInteractor.openTermsOfUse()
    }

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
