package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface AddTrackToHistoryInteractor {
    fun execute(track: Track)
}
