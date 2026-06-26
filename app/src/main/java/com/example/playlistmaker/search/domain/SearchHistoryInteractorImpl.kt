package com.example.playlistmaker.search.domain

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {
    override fun getHistory(): List<Track> = repository.getSearchHistory()
    override fun addTrack(track: Track) = repository.addTrackToHistory(track)
    override fun clearHistory() = repository.clearSearchHistory()
    override fun findTrackById(id: Int): Track? =
        repository.getSearchHistory().find { it.trackId == id }
}