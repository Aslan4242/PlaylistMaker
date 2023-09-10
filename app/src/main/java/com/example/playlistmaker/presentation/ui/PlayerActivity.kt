package com.example.playlistmaker.presentation.ui

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    private val handler = Handler(Looper.getMainLooper())
    private val updateProgressTimeRunnable = Runnable { updateProgressTime() }

    private var player: PlayerInteractor? = Creator.providePlayerInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)
        getTrack(track)

        binding.playButton.setOnClickListener {
            player?.playbackControl({ startPlayer() }, { pausePlayer() })
        }

        preparePlayer()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
        handler.removeCallbacks(updateProgressTimeRunnable)
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

    private fun getTrack(track: Track) {
        binding.nameOfTrack.text = track.trackName
        binding.authorName.text = track.artistName
        binding.durationValue.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())
        if (track.collectionName.isEmpty()) {
            binding.album.visibility = View.GONE
            binding.albumValue.visibility = View.GONE
        } else {
            binding.albumValue.text = track.collectionName
        }
        binding.yearValue.text = track.releaseDate.substring(0, 4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.track_image_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.space_8)))
            .into(binding.trackCover)
    }

    private fun preparePlayer() {
        player?.preparePlayer(track.previewUrl,
            {
                binding.playButton.isEnabled = true
            },
            {
                binding.playButton.apply {
                    if (isNightModeOn()) {
                        setImageResource(R.drawable.ic_play_button_night)
                    } else {
                        setImageResource(R.drawable.ic_play_button)
                    }
                }
                binding.progressTime.text = getString(R.string.time_zero)
                handler.removeCallbacks(updateProgressTimeRunnable)
            }
        )
    }

    private fun isNightModeOn(): Boolean {
        val nightModeFlags =
            binding.playButton.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    private fun startPlayer() {
        binding.playButton.apply {
            if (isNightModeOn()) {
                setImageResource(R.drawable.ic_pause_button_night)
            } else {
                setImageResource(R.drawable.ic_pause_button)
            }
        }
        handler.post(
            updateProgressTimeRunnable
        )
    }

    private fun pausePlayer() {
        binding.playButton.apply {
            if (isNightModeOn()) {
                setImageResource(R.drawable.ic_play_button_night)
            } else {
                setImageResource(R.drawable.ic_play_button)
            }
        }
        handler.removeCallbacks(updateProgressTimeRunnable)
    }

    private fun updateProgressTime() {
        val currentPosition =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(player?.getCurrentPosition())
        binding.progressTime.text = currentPosition
        handler.postDelayed(updateProgressTimeRunnable, UPDATE_PROGRESS_TIME_DELAY)

    }

    companion object {
        private const val TRACK = "TRACK"
        private const val UPDATE_PROGRESS_TIME_DELAY = 200L
    }
}
