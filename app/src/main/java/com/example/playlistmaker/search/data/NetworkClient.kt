package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.ErrorType

interface NetworkClient {
    fun searchTracks(query: String, callback: (List<TrackDto>?, ErrorType?) -> Unit)
}