package com.example.playlistmaker.search.domain

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
    fun findTrackById(id: Int): Track?
}