package com.example.playlistmaker.sharing.domain

class SharingInteractorImpl(
    private val repository: SharingRepository
) : SharingInteractor {
    override fun shareApp() = repository.shareApp()
    override fun writeToSupport() = repository.writeToSupport()
    override fun openTermsOfUse() = repository.openTermsOfUse()
}