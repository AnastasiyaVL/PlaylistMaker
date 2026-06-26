package com.example.playlistmaker.player.ui

import com.example.playlistmaker.search.domain.Track

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()
    data class Content(
        val track: Track,
        val isPlaying: Boolean,
        val currentTime: String,
        val duration: String,
        val formattedDuration: String
    ) : PlayerScreenState()

    data class Error(val message: String) : PlayerScreenState()
}