package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AddTrackToHistoryInteractor
import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track

class AddTrackToHistoryInteractorImpl(
    private val repository: TrackRepository
) : AddTrackToHistoryInteractor {
    override fun execute(track: Track) {
        repository.addTrackToHistory(track)
    }
}
