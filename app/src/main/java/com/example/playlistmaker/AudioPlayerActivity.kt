package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var coverImageView: ImageView
    private lateinit var trackNameTextView: TextView
    private lateinit var artistNameTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var albumLayout: LinearLayout
    private lateinit var albumTextView: TextView
    private lateinit var yearLayout: LinearLayout
    private lateinit var yearTextView: TextView
    private lateinit var genreTextView: TextView
    private lateinit var countryTextView: TextView
    private lateinit var currentTimeTextView: TextView

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        toolbar = findViewById(R.id.toolbar)
        coverImageView = findViewById(R.id.coverImageView)
        trackNameTextView = findViewById(R.id.trackNameTextView)
        artistNameTextView = findViewById(R.id.artistNameTextView)
        durationTextView = findViewById(R.id.durationTextView)
        albumLayout = findViewById(R.id.albumLayout)
        albumTextView = findViewById(R.id.albumTextView)
        yearLayout = findViewById(R.id.yearLayout)
        yearTextView = findViewById(R.id.yearTextView)
        genreTextView = findViewById(R.id.genreTextView)
        countryTextView = findViewById(R.id.countryTextView)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track = intent.getSerializableExtra("track") as? Track

        if (track != null) {
            displayTrackInfo(track)
        }
    }

    private fun displayTrackInfo(track: Track) {
        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName

        val duration = dateFormat.format(track.trackTimeMillis)
        durationTextView.text = duration

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(coverImageView)

        if (!track.collectionName.isNullOrEmpty()) {
            albumTextView.text = track.collectionName
            albumLayout.isVisible = true
        } else {
            albumLayout.isVisible = false
        }

        val year = track.getYear()
        if (!year.isNullOrEmpty()) {
            yearTextView.text = year
            yearLayout.isVisible = true
        } else {
            yearLayout.isVisible = false
        }

        genreTextView.text = track.primaryGenreName ?: "—"
        countryTextView.text = track.country ?: "—"

        currentTimeTextView.text = duration
    }
}
