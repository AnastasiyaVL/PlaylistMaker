package com.example.playlistmaker.data.impl

import com.example.playlistmaker.domain.api.SettingsRepository
import com.example.playlistmaker.data.storage.PlaylistMakerSharedPreferences

class SettingsRepositoryImpl(
    private val storage: PlaylistMakerSharedPreferences
) : SettingsRepository {

    override fun getThemeSettings(): Boolean {
        return storage.getThemeSettings()
    }

    override fun saveThemeSettings(darkThemeEnabled: Boolean) {
        storage.saveThemeSettings(darkThemeEnabled)
    }
}
