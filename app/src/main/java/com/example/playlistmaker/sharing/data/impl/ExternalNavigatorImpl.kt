package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.models.UrlData

class ExternalNavigatorImpl(val context: Context) : ExternalNavigator {

    override fun shareLink(url: UrlData) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, getUrlString(url))
        context.startActivity(
            Intent.createChooser(shareIntent, getUrlTitleString(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override fun openLink(url: UrlData) {
        val termsOfUseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getUrlString(url)))
        termsOfUseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(termsOfUseIntent)
    }

    override fun openEmail() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.mail_address)))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.mail_text))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.theme_text))
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(supportIntent)
    }

    private fun getUrlString(url: UrlData): String {
        return when (url) {
            UrlData.SHARE_APP_URL -> context.getString(R.string.course_link)
            UrlData.TERMS_URL -> context.getString(R.string.offer_link)
        }
    }

    private fun getUrlTitleString(url: UrlData): String {
        return when (url) {
            UrlData.SHARE_APP_URL -> context.getString(R.string.share_app)
            UrlData.TERMS_URL -> ""
        }
    }
}
