package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.GetSearchHistoryInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class GetSearchHistoryInteractorImpl(
    private val repository: TrackRepository
) : GetSearchHistoryInteractor {
    override fun execute(): List<Track> {
        return repository.getSearchHistory()
    }
}
