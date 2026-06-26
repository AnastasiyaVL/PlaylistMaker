package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.sharing.domain.ExternalNavigator
import com.example.playlistmaker.sharing.domain.SharingRepository

class SharingRepositoryImpl(
    private val navigator: ExternalNavigator
) : SharingRepository {
    override fun shareApp() = navigator.shareApp()
    override fun writeToSupport() = navigator.writeToSupport()
    override fun openTermsOfUse() = navigator.openTermsOfUse()
}