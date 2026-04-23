package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPrefs: SharedPreferences) {

    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    private val gson = Gson()

    fun getHistory(): MutableList<Track> {
        val json = sharedPrefs.getString(HISTORY_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Track>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeAll { it.trackId == track.trackId }
        history.add(0, track)
        while (history.size > MAX_HISTORY_SIZE) {
            history.removeAt(history.lastIndex)
        }
        saveHistory(history)
    }

    fun clearHistory() {
        saveHistory(mutableListOf())
    }

    private fun saveHistory(history: MutableList<Track>) {
        val json = gson.toJson(history)
        sharedPrefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }
}
