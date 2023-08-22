package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.track.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        track = Gson().fromJson(intent.getStringExtra(TRACK), Track::class.java)

        getTrack(track)
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

    companion object {
        private const val TRACK = "TRACK"
    }
}
