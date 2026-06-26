package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    private val key = "dark_theme"

    override fun getThemeSettings(): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    override fun saveThemeSettings(darkThemeEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(key, darkThemeEnabled).apply()
    }
}