package com.example.playlistmaker

import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.impl.TrackRepositoryImpl
import com.example.playlistmaker.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.storage.PlaylistMakerSharedPreferences
import com.example.playlistmaker.domain.api.*
import com.example.playlistmaker.domain.impl.*

object Creator {
    private lateinit var app: App

    fun init(app: App) {
        this.app = app
    }

    private fun getTrackRepository(): TrackRepository {
        val sharedPrefs = app.getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE)
        val storage = PlaylistMakerSharedPreferences(sharedPrefs)
        return TrackRepositoryImpl(RetrofitClient.api, storage)
    }

    private fun getSettingsRepository(): SettingsRepository {
        val sharedPrefs = app.getSharedPreferences("app_preferences", android.content.Context.MODE_PRIVATE)
        val storage = PlaylistMakerSharedPreferences(sharedPrefs)
        return SettingsRepositoryImpl(storage)
    }

    fun provideSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractorImpl(getTrackRepository())
    }

    fun provideGetSearchHistoryInteractor(): GetSearchHistoryInteractor {
        return GetSearchHistoryInteractorImpl(getTrackRepository())
    }

    fun provideAddTrackToHistoryInteractor(): AddTrackToHistoryInteractor {
        return AddTrackToHistoryInteractorImpl(getTrackRepository())
    }

    fun provideClearSearchHistoryInteractor(): ClearSearchHistoryInteractor {
        return ClearSearchHistoryInteractorImpl(getTrackRepository())
    }

    fun provideGetThemeSettingsInteractor(): GetThemeSettingsInteractor {
        return GetThemeSettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSaveThemeSettingsInteractor(): SaveThemeSettingsInteractor {
        return SaveThemeSettingsInteractorImpl(getSettingsRepository())
    }
}
