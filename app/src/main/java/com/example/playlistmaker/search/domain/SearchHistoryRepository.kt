package com.example.playlistmaker.search.domain

interface SearchHistoryRepository {
    fun getSearchHistory(): List<Track>
    fun addTrackToHistory(track: Track)
    fun clearSearchHistory()
}