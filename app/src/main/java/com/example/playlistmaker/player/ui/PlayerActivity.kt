package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.google.android.material.appbar.MaterialToolbar

class PlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_ID_KEY = "track_id"
    }

    private lateinit var viewModel: PlayerViewModel

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
    private lateinit var playButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        viewModel = ViewModelProvider(
            this,
            PlayerViewModelFactory(
                Creator.provideMediaPlayerInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        )[PlayerViewModel::class.java]

        initViews()
        setupListeners()
        observeViewModel()

        val trackId = intent.getIntExtra(TRACK_ID_KEY, -1)
        if (trackId != -1) {
            viewModel.loadTrack(trackId)
        }
    }

    private fun initViews() {
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
        playButton = findViewById(R.id.playButton)
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            viewModel.playPause()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }
    }

    private fun renderState(state: PlayerScreenState) {
        when (state) {
            is PlayerScreenState.Loading -> {
            }

            is PlayerScreenState.Content -> {
                displayTrackInfo(state)
                updatePlayButton(state.isPlaying)
                currentTimeTextView.text = state.currentTime
                durationTextView.text = state.formattedDuration
            }

            is PlayerScreenState.Error -> {
            }
        }
    }

    private fun displayTrackInfo(state: PlayerScreenState.Content) {
        val track = state.track

        trackNameTextView.text = track.trackName
        artistNameTextView.text = track.artistName

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
    }

    private fun updatePlayButton(isPlaying: Boolean) {
        playButton.setImageResource(
            if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
        )
    }

    override fun onPause() {
        super.onPause()
        viewModel.releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.releasePlayer()
    }
}