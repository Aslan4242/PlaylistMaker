package com.example.playlistmaker.media.data.impl

import android.content.Context
import android.content.Intent
import com.example.playlistmaker.media.domain.db.ExternalNavigatorMedia

class ExternalNavigatorMediaImpl(private val context: Context): ExternalNavigatorMedia {
    override fun sharePlaylist(playlistInfo: String) {
        try {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, playlistInfo)
            context.startActivity(
                Intent.createChooser(shareIntent, "")
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (_: Exception) {
        }
    }
}