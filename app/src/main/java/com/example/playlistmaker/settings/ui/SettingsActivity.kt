package com.example.playlistmaker.settings.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.shareApp.setOnClickListener {
            viewModel.onShareAppBtnClick()
        }

        binding.support.setOnClickListener {
            viewModel.onSupportBtnClick()
        }

        binding.termsOfUse.setOnClickListener {
            viewModel.onTermsOfUseBtnClick()
        }
    }
}
