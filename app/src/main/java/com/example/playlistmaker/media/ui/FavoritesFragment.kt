package com.example.playlistmaker.media.ui

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
    private lateinit var binding: FragmentFavoritesBinding

    private val viewModel: FavoritesViewModel by viewModel()

    private var favoritesAdapter: FavoritesAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lifecycle.addObserver(viewModel)

        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
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
                track?.let { viewModel.showPlayer(it) }
                findNavController().navigate(
                    R.id.action_mediaFragment_to_playerActivity,
                    track?.let { PlayerActivity.createArgs(it) })
            }
        })

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: FavoriteTracksScreenState) {
        binding.favoritesEmpty.isVisible = state is FavoriteTracksScreenState.Empty
        binding.favoritesRecyclerView.isVisible = state is FavoriteTracksScreenState.Content
        when (state) {
            is FavoriteTracksScreenState.Loading, FavoriteTracksScreenState.Empty -> Unit
            is FavoriteTracksScreenState.Content -> favoritesAdapter?.addItems(state.tracks)
        }
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}