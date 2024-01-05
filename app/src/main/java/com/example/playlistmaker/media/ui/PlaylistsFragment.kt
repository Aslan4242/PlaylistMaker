package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.models.PlaylistsScreenState
import com.example.playlistmaker.media.presentation.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!
    private val playlistsViewModel: PlaylistsViewModel by viewModel()
    private val playlistsAdapter = PlaylistsAdapter(ArrayList())
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(playlistsViewModel)
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvPlaylistGrid.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylistGrid.adapter = playlistsAdapter

        playlistsViewModel.observeState().observe(viewLifecycleOwner) { state ->
            render(state)
        }

        playlistsViewModel.getShowPlaylistInfo().observe(viewLifecycleOwner) { playlistId ->
            showPlaylistInfo(playlistId)
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_addPlaylistFragment)
        }

        playlistsAdapter.setOnItemClickListener(object :
            PlaylistsAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                val playlistId = playlistsAdapter.getPlayListId(position)
                val action =
                    MediaFragmentDirections.actionMediaFragmentToAboutPlaylistFragment(playlistId!!)
                findNavController().navigate(action)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun render(state: PlaylistsScreenState) {
        binding.thereAreNoPlaylists.isVisible = state == PlaylistsScreenState.Empty
        binding.rvPlaylistGrid.isVisible = state is PlaylistsScreenState.Playlists

        when (state) {
            is PlaylistsScreenState.Loading, PlaylistsScreenState.Empty -> Unit
            is PlaylistsScreenState.Playlists -> playlistsAdapter.addItems(state.playlists)
        }
    }

    private fun showPlaylistInfo(playlistId: Long) {
        val action = MediaFragmentDirections.actionMediaFragmentToAboutPlaylistFragment(playlistId)
        findNavController().navigate(action)
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}