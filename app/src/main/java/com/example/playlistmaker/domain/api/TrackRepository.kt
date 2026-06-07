package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.ErrorType

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit)
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}
