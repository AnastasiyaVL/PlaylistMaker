package com.example.playlistmaker.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistMakerSharedPreferences(
    private val sharedPrefs: SharedPreferences
) {
    private val gson = Gson()

    companion object {
        private const val THEME_KEY = "dark_theme"
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun getThemeSettings(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }

    fun saveThemeSettings(darkThemeEnabled: Boolean) {
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()
    }

    fun getSearchHistory(): List<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    fun addTrackToHistory(track: Track) {
        val history = getSearchHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        while (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }
        saveHistory(history)
    }

    fun clearSearchHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
}
