package com.example.playlistmaker.search.domain

interface TrackRepository {
    fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit)
}