package com.example.playlistmaker.domain.api

interface SaveThemeSettingsInteractor {
    fun execute(darkThemeEnabled: Boolean)
}
