package com.example.playlistmaker.search.domain

class TracksInteractorImpl(
    private val repository: TrackRepository
) : TracksInteractor {
    override fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit) {
        repository.searchTracks(query, callback)
    }
}