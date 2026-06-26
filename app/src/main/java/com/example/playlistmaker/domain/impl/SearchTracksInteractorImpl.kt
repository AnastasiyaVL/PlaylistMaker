package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchTracksInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.ErrorType

class SearchTracksInteractorImpl(
    private val repository: TrackRepository
) : SearchTracksInteractor {
    override fun execute(query: String, callback: (List<Track>?, ErrorType?) -> Unit) {
        repository.searchTracks(query, callback)
    }
}
