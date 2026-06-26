package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<Boolean>()
    val themeState: LiveData<Boolean> = _themeState

    private val _shouldRecreate = MutableLiveData<Boolean>()
    val shouldRecreate: LiveData<Boolean> = _shouldRecreate

    init {
        loadTheme()
    }

    fun loadTheme() {
        _themeState.value = settingsInteractor.getThemeSettings()
    }

    fun onThemeChanged(isDarkTheme: Boolean) {
        settingsInteractor.saveThemeSettings(isDarkTheme)
        _themeState.value = isDarkTheme
        _shouldRecreate.value = true
    }

    fun onRecreated() {
        _shouldRecreate.value = false
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun writeToSupport() {
        sharingInteractor.writeToSupport()
    }

    fun openTermsOfUse() {
        sharingInteractor.openTermsOfUse()
    }
}