package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.ErrorType
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TrackRepository

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit) {
        networkClient.searchTracks(query) { dtoList, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val tracks = dtoList?.map { dto ->
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
                } ?: emptyList()
                callback(tracks, null)
            }
        }
    }
}