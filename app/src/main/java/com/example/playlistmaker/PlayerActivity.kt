package com.example.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.track.Track
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class PlayerActivity : AppCompatActivity() {
    private lateinit var track: Track
    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var authorName: TextView
    private lateinit var progressTime: TextView
    private lateinit var duration: TextView
    private lateinit var albumField: TextView
    private lateinit var album: TextView
    private lateinit var year: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val backButton = findViewById<ImageButton>(R.id.back_button)

        backButton.setOnClickListener {
            finish()
        }

        trackCover = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.nameOfTrack)
        authorName = findViewById(R.id.authorName)
        progressTime = findViewById(R.id.progressTime)
        duration = findViewById(R.id.durationValue)
        albumField = findViewById(R.id.album)
        album = findViewById(R.id.albumValue)
        year = findViewById(R.id.yearValue)
        genre = findViewById(R.id.genreValue)
        country = findViewById(R.id.countryValue)

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
        trackName.text = track.trackName
        authorName.text = track.artistName
        duration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())
        if (track.collectionName.isNullOrEmpty()) {
            albumField.visibility = View.GONE
            album.visibility = View.GONE
        } else {
            album.text = track.collectionName
        }
        year.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country
        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.track_image_placeholder)
            .fitCenter()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.space_8)))
            .into(trackCover)
    }

    fun Track.getCoverArtwork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")


    companion object {
        private const val TRACK = "TRACK"
    }
}