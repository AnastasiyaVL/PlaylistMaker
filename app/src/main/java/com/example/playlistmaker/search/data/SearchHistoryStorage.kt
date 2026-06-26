package com.example.playlistmaker.search.data

interface SearchHistoryStorage {
    fun getHistory(): List<TrackDto>
    fun saveHistory(history: List<TrackDto>)
    fun clearHistory()
}