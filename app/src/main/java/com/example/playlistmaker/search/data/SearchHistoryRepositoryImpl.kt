package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchHistoryRepository
import com.example.playlistmaker.search.domain.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage
) : SearchHistoryRepository {

    override fun getSearchHistory(): List<Track> {
        return storage.getHistory().map { dto ->
            Track(
                trackName = dto.trackName,
                artistName = dto.artistName,
                trackTimeMillis = dto.trackTimeMillis,
                artworkUrl100 = dto.artworkUrl100,
                trackId = dto.trackId,
                collectionName = dto.collectionName,
                releaseDate = dto.releaseDate,
                primaryGenreName = dto.primaryGenreName,
                country = dto.country,
                previewUrl = dto.previewUrl
            )
        }
    }

    override fun addTrackToHistory(track: Track) {
        val history = storage.getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, mapToDto(track))
        if (history.size > 10) {
            history.removeAt(history.lastIndex)
        }
        storage.saveHistory(history)
    }

    override fun clearSearchHistory() {
        storage.clearHistory()
    }

    private fun mapToDto(track: Track): TrackDto {
        return TrackDto(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            trackId = track.trackId,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}