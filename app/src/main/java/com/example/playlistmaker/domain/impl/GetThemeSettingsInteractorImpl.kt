package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.GetThemeSettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

class GetThemeSettingsInteractorImpl(
    private val repository: SettingsRepository
) : GetThemeSettingsInteractor {
    override fun execute(): Boolean {
        return repository.getThemeSettings()
    }
}
