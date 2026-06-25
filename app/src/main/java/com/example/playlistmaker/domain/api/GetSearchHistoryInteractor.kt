package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface GetSearchHistoryInteractor {
    fun execute(): List<Track>
}
