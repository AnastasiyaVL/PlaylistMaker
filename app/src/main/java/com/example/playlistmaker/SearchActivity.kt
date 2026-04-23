package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    private var tracks = mutableListOf<Track>()
    private var searchText: String = ""
    private var lastQuery: String = ""
    private lateinit var searchHistory: SearchHistory

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
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

        val sharedPrefs = getSharedPreferences("app_preferences", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearIcon = findViewById(R.id.clearIcon)
        recyclerView = findViewById(R.id.recyclerView)
        emptyPlaceholder = findViewById(R.id.emptyPlaceholder)
        errorPlaceholder = findViewById(R.id.errorPlaceholder)
        retryButton = findViewById(R.id.retryButton)
        historyTitle = findViewById(R.id.historyTitle)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        adapter = TrackAdapter(mutableListOf()) { track ->
            onTrackClick(track)
        }
        recyclerView.adapter = adapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchEditText.setText(searchText)
            clearIcon.isVisible = searchText.isNotEmpty()
            if (searchText.isNotEmpty()) {
                searchTracks(searchText)
            }
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
            searchHistory.clearHistory()
            showHistory()
        }

        searchEditText.requestFocus()
        showKeyboard()
        showHistory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    private fun searchTracks(query: String) {
        lastQuery = query
        showLoading()

        RetrofitClient.api.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                if (response.isSuccessful) {
                    val trackResponse = response.body()
                    if (trackResponse != null && trackResponse.results.isNotEmpty()) {
                        tracks.clear()
                        tracks.addAll(trackResponse.results)
                        showRecyclerView()
                    } else {
                        showEmptyPlaceholder()
                    }
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showErrorPlaceholder()
            }
        })
    }

    private fun showHistory() {
        val history = searchHistory.getHistory()
        if (history.isNotEmpty() && searchEditText.text.isEmpty() && searchEditText.hasFocus()) {
            adapter.updateTracks(history)
            historyTitle.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
            recyclerView.visibility = View.VISIBLE
            emptyPlaceholder.visibility = View.GONE
            errorPlaceholder.visibility = View.GONE
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
        historyTitle.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyPlaceholder.visibility = View.GONE
        errorPlaceholder.visibility = View.GONE
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
        searchHistory.addTrack(track)
    }
}

