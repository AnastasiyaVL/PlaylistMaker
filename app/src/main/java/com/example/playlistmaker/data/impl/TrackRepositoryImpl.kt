package com.example.playlistmaker.data.impl

import com.example.playlistmaker.domain.api.TrackRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.ErrorType
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponseDto
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.storage.PlaylistMakerSharedPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TrackRepositoryImpl(
    private val api: ItunesApi,
    private val storage: PlaylistMakerSharedPreferences
) : TrackRepository {

    override fun searchTracks(query: String, callback: (List<Track>?, ErrorType?) -> Unit) {
        api.searchTracks(query).enqueue(object : Callback<TrackResponseDto> {
            override fun onResponse(
                call: Call<TrackResponseDto>,
                response: Response<TrackResponseDto>
            ) {
                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    if (trackResponse != null && trackResponse.results.isNotEmpty()) {
                        val tracks = trackResponse.results.map { mapToDomain(it) }
                        callback(tracks, null)
                    } else {
                        callback(null, ErrorType.NOTHING_FOUND)
                    }
                } else {
                    callback(null, ErrorType.SERVER_ERROR)
                }
            }

            override fun onFailure(call: Call<TrackResponseDto>, t: Throwable) {
                callback(null, ErrorType.NETWORK_ERROR)
            }
        })
    }

    override fun getSearchHistory(): List<Track> {
        return storage.getSearchHistory()
    }

    override fun addTrackToHistory(track: Track) {
        storage.addTrackToHistory(track)
    }

    override fun clearSearchHistory() {
        storage.clearSearchHistory()
    }

    private fun mapToDomain(dto: TrackDto): Track {
        return Track(
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
