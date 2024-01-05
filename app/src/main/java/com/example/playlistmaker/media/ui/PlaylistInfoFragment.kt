package com.example.playlistmaker.media.ui

import android.content.ContextWrapper
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.media.models.PlaylistInfoScreenState
import com.example.playlistmaker.media.presentation.mapper.ParcelablePlaylistMapper
import com.example.playlistmaker.media.presentation.view_model.PlaylistInfoViewModel
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File

class PlaylistInfoFragment : Fragment() {
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!

    private val args: PlaylistInfoFragmentArgs by navArgs()

    private val playlistInfoViewModel: PlaylistInfoViewModel by viewModel { parametersOf(args.playlistId) }

    private lateinit var deleteTrackDialog: MaterialAlertDialogBuilder
    private lateinit var deletePlaylistDialog: MaterialAlertDialogBuilder

    private lateinit var bottomSheetMenuBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val trackListBottomSheetAdapter = TrackListBottomSheetAdapter(ArrayList()).apply {
        clickListener = TrackListBottomSheetAdapter.TrackClickListener {
            playlistInfoViewModel.showPlayer(it)
        }

        longClickListener = TrackListBottomSheetAdapter.TrackLongClickListener {track ->
            deleteTrackDialog
                .setPositiveButton(R.string.delete) { _, _ ->
                    playlistInfoViewModel.onDeleteTrackClick(track)
                }
                .show()
        }
    }

    private val backPressedOnMenuCallback = object: OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (bottomSheetMenuBehavior.state!= BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetMenuBehavior = BottomSheetBehavior.from(binding.playlistBottomSheetOptionsMenu)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)

        binding.playlistsList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.playlistsList.adapter = trackListBottomSheetAdapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedOnMenuCallback)

        playlistInfoViewModel.observeTrackList().observe(viewLifecycleOwner) { trackList ->
            renderTracks(trackList)
        }

        playlistInfoViewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        playlistInfoViewModel.getShowPlayer().observe(viewLifecycleOwner) {
            showPlayerActivity(it)
        }

        playlistInfoViewModel.getDeletePlaylist().observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        playlistInfoViewModel.getShowPlaylistEdit().observe(viewLifecycleOwner) { playlist ->
            showPlaylistEditFragment(playlist)
        }

        binding.backButton.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.bottomSheetShareButton.setOnClickListener {
            showBottomMenu(isVisible = false)
            sharePlaylist()
        }

        binding.bottomSheetDeleteButton.setOnClickListener {
            showBottomMenu(isVisible = false)
            deletePlaylistDialog.show()
        }

        binding.optionsMenuButton.setOnClickListener {
            showBottomMenu(isVisible = true)
        }

        binding.bottomSheetEditButton.setOnClickListener {
            showBottomMenu(isVisible = false)
            playlistInfoViewModel.showPlaylistEdit()
        }

        bottomSheetMenuBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        backPressedOnMenuCallback.isEnabled = false
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })

        init()
    }

    private fun init() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        showBottomMenu(isVisible = false)
        initDialogs()
    }

    private fun initDialogs() {
        deleteTrackDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.delete_track))
            .setMessage(R.string.are_you_sure_deleting_track)
            .setNegativeButton(R.string.сancel) {_, _ -> }
            .setPositiveButton(R.string.delete) { _, _ -> }

        deletePlaylistDialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(R.string.are_you_sure_deleting_playlist)
            .setNegativeButton(R.string.сancel) {_, _ -> }
            .setPositiveButton(R.string.delete) { _, _ ->
                playlistInfoViewModel.observeState().removeObservers(viewLifecycleOwner)
                playlistInfoViewModel.onDeletePlaylist()
            }
    }


    private fun renderState(state: PlaylistInfoScreenState) {
        when (state) {
            is PlaylistInfoScreenState.Success -> setContent(state.data)
            is PlaylistInfoScreenState.Error -> processError(state.message)
        }
    }

    private fun renderTracks(trackList: List<Track>) {
        binding.bottomSheetPlaylistTrackCount.text =
            playlistInfoViewModel.getTrackCountInfo()
        binding.playlistInfo.text = getString(
            R.string.playlist_info,
            playlistInfoViewModel.getPlaylistTimeInfo(),
            playlistInfoViewModel.getTrackCountInfo()
        )
        trackListBottomSheetAdapter.addItems(trackList)

        binding.thereAreNoTracks.isVisible = trackList.isEmpty()
        binding.playlistsList.isVisible = trackList.isNotEmpty()
    }

    private fun setContent(playlist: Playlist) {
        binding.playlistTitle.text = playlist.name.orEmpty()
        binding.playlistDescription.text = playlist.description.orEmpty()
        binding.playlistCover.setImageResource(R.drawable.track_image_placeholder_large)
        if (!playlist.filePath.isNullOrEmpty()) {
            val file = playlist.filePath?.let { File(getImageDirectory(), it)  }
            binding.playlistCover.setImageURI(Uri.fromFile(file))
            binding.playlistCover.clipToOutline = true

            binding.bottomSheetCoverImage.setImageURI(Uri.fromFile(file))
            binding.bottomSheetCoverImage.clipToOutline = true
        }
        binding.bottomSheetPlaylistName.text =
            playlist.name.orEmpty()
    }

    private fun processError(message: String) {
        showMessage(message)
        findNavController().navigateUp()
    }

    private fun showPlayerActivity(track: Track) {
        findNavController().navigate(R.id.action_playlistInfoFragment_to_playerActivity,
            PlayerActivity.createArgs(track))
    }

    private fun sharePlaylist() {
        if (!playlistInfoViewModel.onSharePlaylist()) {
            showMessage(getString(R.string.share_playlist_empty_message))
        }
    }

    private fun showBottomMenu(isVisible: Boolean) {
        backPressedOnMenuCallback.isEnabled = isVisible
        if (isVisible) {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheetMenuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showPlaylistEditFragment(playlist: Playlist) {
        val action = PlaylistInfoFragmentDirections.actionPlaylistInfoFragmentToPlaylistEditFragment(
            ParcelablePlaylistMapper.map(playlist)
        )
        findNavController().navigate(action)
    }

    private fun showMessage(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    private fun getImageDirectory() = File(
        ContextWrapper(context?.applicationContext).getExternalFilesDir(Environment.DIRECTORY_PICTURES),
        "myPlaylist"
    ).apply { if (!exists()) mkdirs() }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}