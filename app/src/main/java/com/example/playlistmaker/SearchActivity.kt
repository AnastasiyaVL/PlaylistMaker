package com.example.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast

class SearchActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var searchEditText: EditText
    private lateinit var clearIcon: ImageView
    private var searchText: String = ""

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        clearIcon = findViewById(R.id.clearIcon)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
            searchEditText.setText(searchText)
            if (searchText.isNotEmpty()) {
                showClearIcon()
            }
        }

        clearIcon.setOnClickListener {
            searchEditText.text.clear()
            searchText = ""
            hideClearIcon()
            hideKeyboard()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = s?.toString() ?: ""
                if (searchText.isEmpty()) {
                    hideClearIcon()
                } else {
                    showClearIcon()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showKeyboard()
            }
        }

        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
            showKeyboard()
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        searchEditText.setText(searchText)
        if (searchText.isNotEmpty()) {
            showClearIcon()
        } else {
            hideClearIcon()
        }
    }

    private fun showClearIcon() {
        clearIcon.visibility = View.VISIBLE
    }

    private fun hideClearIcon() {
        clearIcon.visibility = View.GONE
    }

    private fun showKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun performSearch() {
        val query = searchEditText.text.toString()
        if (query.isNotEmpty()) {
            Toast.makeText(this, query, Toast.LENGTH_SHORT).show()
        }
    }
}
