package com.example.playlistmaker.creator

import android.app.Activity
import com.example.playlistmaker.App
import com.example.playlistmaker.search.data.*
import com.example.playlistmaker.search.domain.*
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.domain.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.data.SharingRepositoryImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractorImpl
import com.example.playlistmaker.player.data.MediaPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.MediaPlayerInteractor
import com.example.playlistmaker.player.domain.MediaPlayerInteractorImpl
import com.example.playlistmaker.player.domain.MediaPlayerRepository
import com.example.playlistmaker.sharing.domain.SharingRepository

object Creator {
    private lateinit var app: App

    fun init(app: App) {
        this.app = app
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(provideTrackRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(provideSearchHistoryRepository())
    }

    private fun provideTrackRepository(): TrackRepository {
        return TrackRepositoryImpl(provideNetworkClient())
    }

    private fun provideSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSearchHistoryStorage())
    }

    private fun provideNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(ItunesApi.retrofitService)
    }

    private fun provideSearchHistoryStorage(): SearchHistoryStorage {
        return SearchHistoryStorageImpl(
            app.getSharedPreferences(
                "app_preferences",
                android.content.Context.MODE_PRIVATE
            )
        )
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(
            app.getSharedPreferences(
                "app_preferences",
                android.content.Context.MODE_PRIVATE
            )
        )
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(provideSharingRepository())
    }

    private fun provideSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(ExternalNavigatorImpl(app))
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(provideMediaPlayerRepository())
    }

    private fun provideMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }
}