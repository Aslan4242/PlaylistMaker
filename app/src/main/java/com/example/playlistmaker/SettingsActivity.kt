package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            val settingsIntent = Intent(this, MainActivity::class.java)
            startActivity(settingsIntent)
            finish()
        }

        val shareAppButton = findViewById<TextView>(R.id.share_app)

        shareAppButton.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.type = "text/plain"
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(shareAppIntent)
        }

        val supportButton = findViewById<TextView>(R.id.support)

        supportButton.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_address)))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_text))
            }
            startActivity(supportIntent)
        }

        val termsOfUseButton = findViewById<TextView>(R.id.terms_of_use)

        termsOfUseButton.setOnClickListener {
            val url = Uri.parse(getString(R.string.offer_link))
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(termsOfUseIntent)
        }
    }
}