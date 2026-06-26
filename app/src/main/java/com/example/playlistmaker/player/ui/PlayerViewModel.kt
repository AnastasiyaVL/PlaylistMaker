package com.example.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private val _state = MutableLiveData<PlayerScreenState>()
    val state: LiveData<PlayerScreenState> = _state

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            if (mediaPlayerInteractor.isPlaying()) {
                val position = mediaPlayerInteractor.getCurrentPosition()
                val currentState = _state.value as? PlayerScreenState.Content
                if (currentState != null) {
                    _state.value = currentState.copy(
                        currentTime = dateFormat.format(position)
                    )
                    handler.postDelayed(this, 500)
                }
            }
        }
    }

    fun loadTrack(trackId: Int) {
        _state.value = PlayerScreenState.Loading
        val track = searchHistoryInteractor.findTrackById(trackId)
        if (track != null) {
            val duration = dateFormat.format(track.trackTimeMillis)
            _state.value = PlayerScreenState.Content(
                track = track,
                isPlaying = false,
                currentTime = "00:00",
                duration = duration,
                formattedDuration = duration
            )
            preparePlayer(track.previewUrl)
        } else {
            _state.value = PlayerScreenState.Error("Трек не найден")
        }
    }

    fun playPause() {
        val state = _state.value
        if (state is PlayerScreenState.Content) {
            if (state.isPlaying) {
                pausePlayer()
            } else {
                startPlayer()
            }
        }
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
        handler.removeCallbacks(updateTimeRunnable)
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun preparePlayer(previewUrl: String?) {
        if (previewUrl.isNullOrEmpty()) {
            _state.value = PlayerScreenState.Error("Нет доступа к аудио")
            return
        }

        mediaPlayerInteractor.preparePlayer(
            previewUrl = previewUrl,
            onPrepared = {
                val currentState = _state.value as? PlayerScreenState.Content
                if (currentState != null) {
                    _state.value = currentState.copy(currentTime = "00:00")
                }
            },
            onCompletion = {
                resetPlayback()
            }
        )
    }

    private fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        val state = _state.value as? PlayerScreenState.Content
        if (state != null) {
            _state.value = state.copy(isPlaying = true)
        }
        handler.post(updateTimeRunnable)
    }

    private fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        val state = _state.value as? PlayerScreenState.Content
        if (state != null) {
            _state.value = state.copy(isPlaying = false)
        }
        handler.removeCallbacks(updateTimeRunnable)
    }

    private fun resetPlayback() {
        handler.removeCallbacks(updateTimeRunnable)
        val state = _state.value as? PlayerScreenState.Content
        if (state != null) {
            _state.value = state.copy(
                isPlaying = false,
                currentTime = "00:00"
            )
        }
    }
}