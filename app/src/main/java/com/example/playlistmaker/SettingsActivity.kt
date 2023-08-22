package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            val shareAppIntent = Intent(Intent.ACTION_SEND)
            shareAppIntent.type = "text/plain"
            shareAppIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.course_link))
            startActivity(shareAppIntent)
        }

        binding.support.setOnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_address)))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.theme_text))
            }
            startActivity(supportIntent)
        }

        binding.termsOfUse.setOnClickListener {
            val url = Uri.parse(getString(R.string.offer_link))
            val termsOfUseIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(termsOfUseIntent)
        }
    }
}