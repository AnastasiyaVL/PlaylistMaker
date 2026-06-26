package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.ErrorType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitNetworkClient(
    private val api: ItunesApi
) : NetworkClient {

    override fun searchTracks(query: String, callback: (List<TrackDto>?, ErrorType?) -> Unit) {
        api.searchTracks(query).enqueue(object : Callback<TrackResponseDto> {
            override fun onResponse(
                call: Call<TrackResponseDto>,
                response: Response<TrackResponseDto>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.results.isNotEmpty()) {
                        callback(body.results, null)
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
}