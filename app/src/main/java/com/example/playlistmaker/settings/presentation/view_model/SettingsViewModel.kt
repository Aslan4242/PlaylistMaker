package com.example.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    application: Application
) : AndroidViewModel(application) {
    fun onShareAppBtnClick() {
        sharingInteractor.shareApp()
    }

    fun onSupportBtnClick() {
        sharingInteractor.openSupport()
    }

    fun onTermsOfUseBtnClick() {
        sharingInteractor.openTermsOfUse()
    }
}
