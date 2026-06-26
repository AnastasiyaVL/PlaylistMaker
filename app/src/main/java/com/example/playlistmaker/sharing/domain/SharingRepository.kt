package com.example.playlistmaker.sharing.domain

interface SharingRepository {
    fun shareApp()
    fun writeToSupport()
    fun openTermsOfUse()
}