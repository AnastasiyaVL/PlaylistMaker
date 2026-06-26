package com.example.playlistmaker.settings.domain

class SettingsInteractorImpl(
    private val repository: SettingsRepository
) : SettingsInteractor {
    override fun getThemeSettings(): Boolean = repository.getThemeSettings()
    override fun saveThemeSettings(darkThemeEnabled: Boolean) =
        repository.saveThemeSettings(darkThemeEnabled)
}