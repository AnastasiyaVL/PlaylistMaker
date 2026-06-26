package com.example.playlistmaker.search.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesApi {
    @GET("/search?entity=song")
    fun searchTracks(@Query("term") text: String): Call<TrackResponseDto>

    companion object {
        private const val BASE_URL = "https://itunes.apple.com/"
        val retrofitService: ItunesApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ItunesApi::class.java)
        }
    }
}