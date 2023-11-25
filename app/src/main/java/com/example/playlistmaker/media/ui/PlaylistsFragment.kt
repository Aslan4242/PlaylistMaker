package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavouritesBinding
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.models.PlaylistsScreenState
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val playlistsViewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistsViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistsScreenState) {
        binding.thereAreNoPlaylists.isVisible = state == PlaylistsScreenState.Empty
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}