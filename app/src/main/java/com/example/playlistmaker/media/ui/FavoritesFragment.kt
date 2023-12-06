package com.example.playlistmaker.media.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.media.models.FavoriteTracksScreenState
import com.example.playlistmaker.media.presentation.view_model.FavoritesViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment: Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesViewModel: FavoritesViewModel by viewModel()

    private var favoritesAdapter: FavoritesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(favoritesViewModel)

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesAdapter = FavoritesAdapter(ArrayList())

        binding.favoritesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.favoritesRecyclerView.adapter = favoritesAdapter

        favoritesAdapter?.setOnItemClickListener(object : FavoritesAdapter.OnListElementClickListener {
            override fun onListElementClick(position: Int) {
                val track = favoritesAdapter?.getTrack(position)
                track?.let { favoritesViewModel.showPlayer(it) }
                findNavController().navigate(
                    R.id.action_mediaFragment_to_playerActivity,
                    track?.let { PlayerActivity.createArgs(it) })
            }
        })

        favoritesViewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun render(state: FavoriteTracksScreenState) {
        binding.favoritesEmpty.isVisible = state is FavoriteTracksScreenState.Empty
        binding.favoritesRecyclerView.isVisible = state is FavoriteTracksScreenState.Content
        when (state) {
            is FavoriteTracksScreenState.Loading, FavoriteTracksScreenState.Empty -> Unit
            is FavoriteTracksScreenState.Content -> favoritesAdapter?.apply {
                this.tracks.clear()
                if (state.tracks.isNotEmpty()) {
                    this.tracks.addAll(state.tracks)
                }
                this.notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}