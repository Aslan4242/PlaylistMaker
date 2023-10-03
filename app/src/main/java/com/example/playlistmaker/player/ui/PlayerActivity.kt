package com.example.playlistmaker.player.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.presentation.mapper.ParcelableTrackMapper
import com.example.playlistmaker.player.presentation.models.ParcelableTrack
import com.example.playlistmaker.player.presentation.models.PlayerScreenState
import com.example.playlistmaker.player.presentation.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        track = getCurrentTrack()
        getTrack(track)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.factory(track)
        )[PlayerViewModel::class.java]

        viewModel.observeState().observe(this) {
            render(it)
        }

        binding.playButton.setOnClickListener {
            viewModel.playBackControl()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TRACK, Gson().toJson(track))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        track = Gson().fromJson(savedInstanceState.getString(TRACK), Track::class.java)
        getTrack(track)
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

    private fun onGetProgressState(time: String?) {
        setTime(time)
    }

    private fun setTime(time: String?) {
        if (!time.isNullOrEmpty()) {
            binding.progressTime.text = time
        }
    }

    private fun getTrack(track: Track) {
        binding.nameOfTrack.text = track.trackName
        binding.authorName.text = track.artistName
        binding.durationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?.toInt())
        if (track.collectionName?.isEmpty() == true) {
            binding.album.visibility = View.GONE
            binding.albumValue.visibility = View.GONE
        } else {
            binding.albumValue.text = track.collectionName
        }
        binding.yearValue.text = track.releaseDate?.substring(0, 4)
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

        fun show(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(TRACK, ParcelableTrackMapper.map(track))

            context.startActivity(intent)
        }
    }
}
