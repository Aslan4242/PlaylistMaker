package com.example.playlistmaker.settings.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.presentation.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switcher.setOnCheckedChangeListener { switcher, checked ->
            viewModel.onSwitchThemeBtnClick(checked)
        }

        viewModel.observeAppTheme().observe(this.viewLifecycleOwner) { isChecked ->
            binding.switcher.isChecked = isChecked
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
