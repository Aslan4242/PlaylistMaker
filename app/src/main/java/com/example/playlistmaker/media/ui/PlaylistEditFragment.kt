package com.example.playlistmaker.media.ui

import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.playlistmaker.R
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.presentation.mapper.ParcelablePlaylistMapper
import com.example.playlistmaker.media.presentation.view_model.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistEditFragment: NewPlaylistFragment() {
    private val args: PlaylistEditFragmentArgs by navArgs()

     override val newPlaylistsViewModel: PlaylistEditViewModel by viewModel {
        parametersOf(ParcelablePlaylistMapper.map(args.playlist))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewAttributes()

        val playlist = ParcelablePlaylistMapper.map(args.playlist)
        initPlaylistData(playlist)
    }

    private fun initPlaylistData(playlist: Playlist) {
        binding.playlistNameField.editText?.setText(playlist.name)
        binding.playlistDescriptionField.editText?.setText(playlist.description)
        binding.addPlaylistButton.setImageResource(R.drawable.track_image_placeholder_large)
        if (!playlist.filePath.isNullOrEmpty()) {
            val file = playlist.filePath?.let { File(getImageDirectory(), it) }
            binding.addPlaylistButton.setImageURI(Uri.fromFile(file))
            binding.addPlaylistButton.clipToOutline = true
        }
    }

    private fun getImageDirectory() = File(
        ContextWrapper(context?.applicationContext).getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "myPlaylist"
    ).apply { if (!exists()) mkdirs() }

    private fun setViewAttributes() {
        binding.createPlaylistButton.setText(R.string.save)
        binding.newPlaylistTitle.setText(R.string.edit)
    }
}