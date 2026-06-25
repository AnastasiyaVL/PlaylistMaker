package com.example.playlistmaker.presentation.audio_player

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        const val TRACK_ID_KEY = "track_id"
    }

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

    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    private var currentTrack: Track? = null

    private val getSearchHistoryInteractor by lazy { Creator.provideGetSearchHistoryInteractor() }

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    val currentPosition = it.currentPosition
                    currentTimeTextView.text =
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(currentPosition)
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        initViews()
        setupListeners()
        loadTrack()
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
            releaseMediaPlayer()
            finish()
        }

        playButton.setOnClickListener {
            togglePlayback()
        }
    }

    private fun loadTrack() {
        val trackId = intent.getIntExtra(TRACK_ID_KEY, -1)
        if (trackId != -1) {
            val track = getSearchHistoryInteractor.execute().find { it.trackId == trackId }
            if (track != null) {
                currentTrack = track
                displayTrackInfo(track)
                setupMediaPlayer(track.previewUrl)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun setupMediaPlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) {
            playButton.isEnabled = false
            return
        }

        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playButton.isEnabled = true
                durationTextView.text = dateFormat.format(it.duration)
            }
            setOnCompletionListener {
                resetPlaybackState()
            }
        }
    }

    private fun togglePlayback() {
        if (isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        mediaPlayer?.start()
        isPlaying = true
        playButton.setImageResource(R.drawable.ic_pause)
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayback() {
        mediaPlayer?.pause()
        isPlaying = false
        playButton.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun resetPlaybackState() {
        isPlaying = false
        playButton.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(updateTimeRunnable)
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                it.seekTo(0)
            }
        }
        currentTimeTextView.text = "00:00"
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateTimeRunnable)
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

        currentTimeTextView.text = "00:00"
    }
}
