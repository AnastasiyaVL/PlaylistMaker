package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SaveThemeSettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class SaveThemeSettingsInteractorImpl(
    private val repository: SettingsRepository
) : SaveThemeSettingsInteractor {
    override fun execute(darkThemeEnabled: Boolean) {
        repository.saveThemeSettings(darkThemeEnabled)
    }
}
