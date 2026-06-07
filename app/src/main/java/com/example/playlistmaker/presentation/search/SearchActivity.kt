package com.example.playlistmaker.presentation.search

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.audio_player.AudioPlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.ErrorType
import com.example.playlistmaker.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearIcon: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackAdapter
    private lateinit var emptyPlaceholder: LinearLayout
    private lateinit var errorPlaceholder: LinearLayout
    private lateinit var retryButton: MaterialButton
    private lateinit var historyTitle: TextView
    private lateinit var clearHistoryButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var tracks = mutableListOf<Track>()
    private var searchText: String = ""
    private var lastQuery: String = ""

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val searchTracksInteractor by lazy { Creator.provideSearchTracksInteractor() }
    private val getSearchHistoryInteractor by lazy { Creator.provideGetSearchHistoryInteractor() }
    private val addTrackToHistoryInteractor by lazy { Creator.provideAddTrackToHistoryInteractor() }
    private val clearSearchHistoryInteractor by lazy { Creator.provideClearSearchHistoryInteractor() }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        val searchLayout = findViewById<LinearLayout>(R.id.searchLayout)
        ViewCompat.setOnApplyWindowInsetsListener(searchLayout) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(
                top = statusBar.top,
                bottom = navigationBar.bottom
            )
            insets
        }

        initViews()
        setupListeners()
        restoreState(savedInstanceState)

        searchEditText.requestFocus()
        showKeyboard()
        showHistory()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearIcon = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
        historyTitle = findViewById(R.id.historyTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        adapter = TrackAdapter(mutableListOf()) { track ->
            onTrackClick(track)
        }
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            searchText = ""
            clearIcon.isVisible = false
            hideKeyboard()
            tracks.clear()
            adapter.updateTracks(tracks)
            showHistory()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearIcon.isVisible = !s.isNullOrEmpty()
                if (searchText.isEmpty()) {
                    showHistory()
                    searchRunnable?.let { handler.removeCallbacks(it) }
                } else {
                    searchDebounce()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                showHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.isNotEmpty()) {
                    searchRunnable?.let { handler.removeCallbacks(it) }
                    searchTracks(searchText)
                }
                true
            } else {
                false
            }
        }

        retryButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                searchTracks(lastQuery)
            }
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistoryInteractor.execute()
            showHistory()
        }
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchEditText.setText(searchText)
            clearIcon.isVisible = searchText.isNotEmpty()
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { handler.removeCallbacks(it) }
    }

    private fun searchDebounce() {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable {
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
        }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun searchTracks(query: String) {
        lastQuery = query
        showLoading()

        searchTracksInteractor.execute(query) { foundTracks, errorType ->
            showContent()
            when {
                errorType == null && !foundTracks.isNullOrEmpty() -> {
                    tracks.clear()
                    tracks.addAll(foundTracks)
                    showRecyclerView()
                }
                errorType == ErrorType.NOTHING_FOUND -> {
                    showEmptyPlaceholder()
                }
                else -> {
                    showErrorPlaceholder()
                }
            }
        }
    }

    private fun showHistory() {
        val history = getSearchHistoryInteractor.execute()
        if (history.isNotEmpty() && searchEditText.text.isEmpty() && searchEditText.hasFocus()) {
            adapter.updateTracks(history)
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            emptyPlaceholder.visibility = View.GONE
            errorPlaceholder.visibility = View.GONE
            progressBar.visibility = View.GONE
        } else {
            historyTitle.visibility = View.GONE
            clearHistoryButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
        }
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
    }

    private fun showRecyclerView() {
        adapter.updateTracks(tracks)
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showEmptyPlaceholder() {
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.VISIBLE
        errorPlaceholder.visibility = View.GONE
    }

    private fun showErrorPlaceholder() {
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.VISIBLE
    }

    private fun onTrackClick(track: Track) {
        addTrackToHistoryInteractor.execute(track)
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(AudioPlayerActivity.TRACK_ID_KEY, track.trackId)
        startActivity(intent)
    }
}
