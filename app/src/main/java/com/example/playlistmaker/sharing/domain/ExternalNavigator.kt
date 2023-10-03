package com.example.playlistmaker.sharing.domain

import com.example.playlistmaker.sharing.domain.models.UrlData

interface ExternalNavigator {
    fun shareLink(url: UrlData)
    fun openLink(url: UrlData)
    fun openEmail()
}
