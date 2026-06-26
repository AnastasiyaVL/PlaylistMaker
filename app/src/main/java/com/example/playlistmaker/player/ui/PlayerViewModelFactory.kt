package com.example.playlistmaker.player.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.search.domain.SearchHistoryInteractor

class PlayerViewModelFactory(
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(mediaPlayerInteractor, searchHistoryInteractor) as T
    }
}