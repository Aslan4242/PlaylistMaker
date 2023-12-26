package com.example.playlistmaker.media.ui

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.media.models.NewPlaylistScreenState
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.models.NewPlayListState
import com.example.playlistmaker.media.presentation.view_model.NewPlaylistViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class NewPlaylistFragment : Fragment() {
    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val newPlaylistsViewModel: NewPlaylistViewModel by viewModel()
    lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (newPlaylistsViewModel.needShowDialog()) {
                confirmDialog.show()
            } else {
                newPlaylistsViewModel.onCancelPlaylist()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    newPlaylistsViewModel.setPlayListImage()
                    binding.addPlaylistButton.apply {
                        setImageURI(uri)
                        clipToOutline = true
                        tag = uri
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        newPlaylistsViewModel.observeState().observe(viewLifecycleOwner) {
            binding.createPlaylistButton.isEnabled = it is NewPlaylistScreenState.PlayListIsNotEmpty

        }

        newPlaylistsViewModel.getAddPlaylist().observe(viewLifecycleOwner) {
            addPlaylist(it)
        }

        newPlaylistsViewModel.resultState().observe(viewLifecycleOwner) { newPlaylistState ->
            when (newPlaylistState) {
                is NewPlayListState.SaveError -> {
                    setFragmentResult(
                        RESULT_KEY,
                        bundleOf(BUNDLE_DATA_KEY to 0, BUNDLE_DATA_NAME to "")
                    )
                    navigateBack()
                }

                is NewPlayListState.SaveSuccess -> {
                    setFragmentResult(
                        RESULT_KEY,
                        bundleOf(
                            BUNDLE_DATA_KEY to newPlaylistState.playlist.id,
                            BUNDLE_DATA_NAME to newPlaylistState.playlist.name
                        )
                    )
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.playlist_created, newPlaylistState.playlist.name),
                        Toast.LENGTH_LONG
                    ).show()
                    navigateBack()
                }
            }
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.finish_playlist_creating)
            .setMessage(R.string.you_might_loose_data)
            .setNeutralButton(R.string.сancel) { dialog, which ->
                // ничего не делаем
            }.setNegativeButton(R.string.finish) { dialog, which ->
                newPlaylistsViewModel.onCancelPlaylist()
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.playlistNameField.editText?.doOnTextChanged { text, _, _, _ ->
            newPlaylistsViewModel.onPlaylistNameChanged(text.toString())
        }

        binding.playlistDescriptionField.editText?.doOnTextChanged { text, _, _, _ ->
            newPlaylistsViewModel.onPlaylistDescriptionChanged(text.toString())
        }

        binding.addPlaylistButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistButton.setOnClickListener {
            newPlaylistsViewModel.onAddPlaylistClick()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(NAME, binding.playlistNameField.editText?.text.toString())
        outState.putString(
            DESCRIPTION_TEXT,
            binding.playlistDescriptionField.editText?.text.toString()
        )
        if (binding.addPlaylistButton.tag != null) {
            outState.putString(IMAGE, (binding.addPlaylistButton.tag as Uri).toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        binding.playlistNameField.editText?.setText(savedInstanceState?.getString(NAME))
        binding.playlistDescriptionField.editText?.setText(
            savedInstanceState?.getString(
                DESCRIPTION_TEXT
            )
        )
        val stringUri = savedInstanceState?.getString(IMAGE)
        if (!stringUri.isNullOrEmpty()) {
            if (Uri.parse(stringUri) != null) {
                binding.addPlaylistButton.apply {
                    setImageURI(Uri.parse(stringUri))
                    clipToOutline = true
                    tag = Uri.parse(stringUri)
                }
            }
        }
    }

    private fun addPlaylist(playlist: Playlist) {
        val dir = File(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myPlaylist")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        if (binding.addPlaylistButton.tag != null && !playlist.filePath.isNullOrEmpty()) {
            val uri: Uri = binding.addPlaylistButton.tag as Uri
            newPlaylistsViewModel.setPlayListImage(
                requireActivity().contentResolver.openInputStream(uri),
                dir
            )
        }
        newPlaylistsViewModel.addPlaylist(playlist)
    }

    private fun navigateBack() {
        if (tag.equals(PlayerActivity.NEW_PLAYLIST_TAG)) {
            parentFragmentManager.popBackStack()
        } else {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RESULT_KEY = "playlist"
        const val BUNDLE_DATA_KEY = "id"
        const val BUNDLE_DATA_NAME = "name"
        private const val IMAGE = "IMAGE"
        private const val NAME = "NAME"
        private const val DESCRIPTION_TEXT = "DESCRIPTION_TEXT"
    }
}