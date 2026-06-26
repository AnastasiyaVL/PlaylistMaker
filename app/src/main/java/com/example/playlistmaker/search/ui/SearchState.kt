package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.ErrorType

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class History(val tracks: List<Track>) : SearchState()
    object Empty : SearchState()
    data class Error(val errorType: ErrorType) : SearchState()
}