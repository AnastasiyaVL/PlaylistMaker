package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryStorageImpl(
    private val sharedPreferences: SharedPreferences
) : SearchHistoryStorage {

    private val gson = Gson()
    private val key = "search_history"

    override fun getHistory(): List<TrackDto> {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            val type = object : TypeToken<List<TrackDto>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    override fun saveHistory(history: List<TrackDto>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(key, json).apply()
    }

    override fun clearHistory() {
        sharedPreferences.edit().remove(key).apply()
    }
}