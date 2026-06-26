package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.ui.PlayerActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

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

    private var searchText: String = ""

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

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        )[SearchViewModel::class.java]

        initViews()
        setupListeners()
        observeViewModel()
        restoreState(savedInstanceState)

        searchEditText.requestFocus()
        showKeyboard()
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

        adapter = TrackAdapter(emptyList()) { track ->
            viewModel.onTrackClick(track)
        }
        recyclerView.adapter = adapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }

        retryButton.setOnClickListener {
            if (searchText.isNotEmpty()) {
                viewModel.searchImmediately(searchText)
            }
        }
    }

    private fun setupListeners() {
        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            searchText = ""
            clearIcon.isVisible = false
            hideKeyboard()
            viewModel.loadHistory()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                clearIcon.isVisible = searchText.isNotEmpty()
                viewModel.searchDebounce(searchText)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) {
                viewModel.loadHistory()
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.isNotEmpty()) {
                    viewModel.searchImmediately(searchText)
                }
                true
            } else {
                false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }

        viewModel.navigateToPlayer.observe(this) { trackId ->
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.TRACK_ID_KEY, trackId)
            startActivity(intent)
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Idle -> {
                hideAll()
            }

            is SearchState.Loading -> {
                showLoading()
            }

            is SearchState.Content -> {
                adapter.updateTracks(state.tracks)
                showRecyclerView()
            }

            is SearchState.History -> {
                adapter.updateTracks(state.tracks)
                showHistory()
            }

            is SearchState.Empty -> {
                showEmptyPlaceholder()
            }

            is SearchState.Error -> {
                showErrorPlaceholder()
            }
        }
    }

    private fun hideAll() {
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        recyclerView.isVisible = false
        emptyPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
        progressBar.isVisible = false
    }

    private fun showHistory() {
        historyTitle.isVisible = true
        clearHistoryButton.isVisible = true
        recyclerView.isVisible = true
        emptyPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
        progressBar.isVisible = false
    }

    private fun showLoading() {
        progressBar.isVisible = true
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        recyclerView.isVisible = false
        emptyPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
    }

    private fun showRecyclerView() {
        recyclerView.isVisible = true
        emptyPlaceholder.isVisible = false
        errorPlaceholder.isVisible = false
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun showEmptyPlaceholder() {
        recyclerView.isVisible = false
        emptyPlaceholder.isVisible = true
        errorPlaceholder.isVisible = false
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun showErrorPlaceholder() {
        recyclerView.isVisible = false
        emptyPlaceholder.isVisible = false
        errorPlaceholder.isVisible = true
        historyTitle.isVisible = false
        clearHistoryButton.isVisible = false
        progressBar.isVisible = false
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchEditText.setText(searchText)
            clearIcon.isVisible = searchText.isNotEmpty()
            if (searchText.isNotEmpty()) {
                viewModel.searchImmediately(searchText)
            } else {
                viewModel.loadHistory()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }
}