package com.example.playlistmaker.player.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.media.ui.NewPlaylistFragment
import com.example.playlistmaker.player.presentation.mapper.ParcelableTrackMapper
import com.example.playlistmaker.player.presentation.models.ParcelableTrack
import com.example.playlistmaker.player.presentation.models.PlayerPlaylistState
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.models.PlayListTrackState
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel { parametersOf(track) }
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private val bottomSheetAdapter = BottomSheetAdapter(ArrayList()).apply {
        clickListener = BottomSheetAdapter.PlaylistClickListener {
            viewModel.addTrackToPlaylist(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomSheetContainer = binding.standardBottomSheet
        val overlay = findViewById<View>(R.id.overlay)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.rvPlaylistList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylistList.adapter = bottomSheetAdapter

        track = getCurrentTrack()

        getTrack(track)
        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeFavoriteState().observe(this) {
            renderAddToFavoriteButton(it)
        }

        binding.addToFavoriteButton.setOnClickListener {
            viewModel.clickOnAddToFavoriteButton()
        }

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }

        binding.addToQueueButton.setOnClickListener {
            viewModel.onPlayerAddTrackClick()
        }

        binding.btNewPlaylist.setOnClickListener {
            supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.player_container_view, NewPlaylistFragment(), NEW_PLAYLIST_TAG)
                    .addToBackStack(null)
                    .commit()
            }

            supportFragmentManager.setFragmentResultListener(
                NewPlaylistFragment.RESULT_KEY,
                this
            ) { _, bundle ->
                val playlistId = bundle.getLong(NewPlaylistFragment.BUNDLE_DATA_KEY)
                val playlistName = bundle.getString(NewPlaylistFragment.BUNDLE_DATA_NAME)
                if (playlistId > 0) {
                    viewModel.addTrackToPlaylist(playlistId, playlistName)
                } else {
                    viewModel.onPlayerAddTrackClick()
                }
            }

            viewModel.onNewPlaylistClick()
        }

        viewModel.observeMode().observe(this) {
            renderMode(it)
        }

        viewModel.getAddProcessStatus().observe(this) {
            renderAddProcessStatus(it)
        }
    }

    private fun renderMode(mode: PlayerPlaylistState) {
        binding.playerContainerView.isVisible = mode is PlayerPlaylistState.NewPlaylist
        binding.playerConstraintView.isVisible = mode is PlayerPlaylistState.Player || mode is PlayerPlaylistState.BottomSheet
        binding.overlay.isVisible = mode is PlayerPlaylistState.BottomSheet
        binding.standardBottomSheet.isVisible = mode is PlayerPlaylistState.BottomSheet

        if (mode is PlayerPlaylistState.BottomSheet) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            bottomSheetAdapter.addItems(mode.playlists)
        }
    }

    private fun renderAddProcessStatus(state: PlayListTrackState) {
        when (state) {
            is PlayListTrackState.TrackIsAdded -> showMessage(getString(R.string.added_to_playlist, state.name))
            is PlayListTrackState.TrackExist -> showMessage(getString(R.string.track_already_added, state.name))
        }
    }

    private fun showMessage(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()


    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun render(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.Prepared -> onGetPreparedState()
            is PlayerScreenState.Playing -> onGetPlayingState()
            is PlayerScreenState.Progress -> onGetProgressState(state.time)
            is PlayerScreenState.Paused -> onGetPausedState(state.time)
        }
    }

    private fun onGetPreparedState() {
        binding.playButton.apply {
            if (isNightModeOn()) {
                setImageResource(R.drawable.ic_play_button_night)
            } else {
                setImageResource(R.drawable.ic_play_button)
            }
        }
        binding.progressTime.text = getString(R.string.time_zero)
        binding.playButton.isEnabled = true
    }

    private fun onGetPlayingState() {
        binding.playButton.apply {
            if (isNightModeOn()) {
                setImageResource(R.drawable.ic_pause_button_night)
            } else {
                setImageResource(R.drawable.ic_pause_button)
            }
        }
    }

    private fun onGetPausedState(time: String?) {
        setTime(time)
        binding.playButton.apply {
            if (isNightModeOn()) {
                setImageResource(R.drawable.ic_play_button_night)
            } else {
                setImageResource(R.drawable.ic_play_button)
            }
        }
    }

    private fun renderAddToFavoriteButton(isFavorite: Boolean) {
        val settingsInteractor: SettingsInteractor = getKoin().get()
        val isDark = settingsInteractor.getDarkTheme()
        val trackNotAddedImage = if (isDark) R.drawable.ic_add_to_favorite_night else
            R.drawable.ic_add_to_favorite
        val trackAddedImage = if (isDark) R.drawable.ic_track_added_to_favorites_button_night else
            R.drawable.ic_track_added_to_favorites_button
        if (isFavorite) {
            binding.addToFavoriteButton.setImageResource(trackAddedImage)
        } else {
            binding.addToFavoriteButton.setImageResource(trackNotAddedImage)
        }
    }

    private fun onGetProgressState(time: String?) {
        setTime(time)
    }

    private fun setTime(time: String?) {
        if (!time.isNullOrEmpty()) {
            binding.progressTime.text = time
        }
    }

    private fun getTrack(track: Track) {
        binding.trackName.text = track.trackName
        binding.authorName.text = track.artistName
        binding.durationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?.toInt())
        if (track.collectionName?.isEmpty() == true) {
            binding.album.visibility = View.GONE
            binding.albumValue.visibility = View.GONE
        } else {
            binding.albumValue.text = track.collectionName
        }
        binding.yearValue.text = track.releaseDate?.toString()
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.track_image_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.space_8)))
            .into(binding.trackCover)
    }

    private fun getCurrentTrack(): Track {
        val track: ParcelableTrack? = intent.getParcelableExtra(TRACK)
        return ParcelableTrackMapper.map(track ?: ParcelableTrack())
    }

    private fun isNightModeOn(): Boolean {
        val nightModeFlags =
            binding.playButton.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        private const val TRACK = "TRACK"
        const val NEW_PLAYLIST_TAG = "player"
        fun createArgs(track: Track): Bundle = bundleOf(TRACK to ParcelableTrackMapper.map(track))
    }
}
