package com.example.playlistmaker.search.domain

interface TracksInteractor {
    fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit)
}