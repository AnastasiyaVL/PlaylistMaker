package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.*
import com.example.playlistmaker.utils.SingleLiveEvent

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val _navigateToPlayer = SingleLiveEvent<Int>()
    val navigateToPlayer: LiveData<Int> = _navigateToPlayer

    private var lastQuery = ""

    init {
        loadHistory()
    }

    fun searchDebounce(query: String) {
        if (query.isEmpty()) {
            loadHistory()
            return
        }

        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            searchTracks(query)
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchImmediately(query: String) {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchTracks(query)
    }

    fun loadHistory() {
        val history = searchHistoryInteractor.getHistory()
        if (history.isNotEmpty()) {
            _state.value = SearchState.History(history)
        } else {
            _state.value = SearchState.Idle
        }
    }

    fun onTrackClick(track: Track) {
        searchHistoryInteractor.addTrack(track)
        _navigateToPlayer.value = track.trackId
    }

    fun clearHistory() {
        searchHistoryInteractor.clearHistory()
        _state.value = SearchState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun searchTracks(query: String) {
        lastQuery = query
        _state.value = SearchState.Loading

        tracksInteractor.searchTracks(query) { foundTracks, errorType ->
            when {
                errorType == null && !foundTracks.isNullOrEmpty() -> {
                    _state.value = SearchState.Content(foundTracks)
                }

                errorType == ErrorType.NOTHING_FOUND -> {
                    _state.value = SearchState.Empty
                }

                else -> {
                    _state.value = SearchState.Error(errorType ?: ErrorType.NETWORK_ERROR)
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}