package com.example.playlistmaker.settings.presentation.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SettingsViewModel(
                    Creator.provideSharingInteractor(application),
                    application
                )
            }
        }
    }
}
