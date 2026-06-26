package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getThemeSettings(): Boolean
    fun saveThemeSettings(darkThemeEnabled: Boolean)
}