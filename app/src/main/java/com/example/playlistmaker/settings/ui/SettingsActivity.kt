package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var darkThemeSwitch: SwitchMaterial
    private var isUpdatingFromCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(
                Creator.provideSettingsInteractor(),
                Creator.provideSharingInteractor()
            )
        )[SettingsViewModel::class.java]

        (applicationContext as App).applyTheme(viewModel.themeState.value ?: false)

        val settingsMain = findViewById<LinearLayout>(R.id.settingsMain)
        ViewCompat.setOnApplyWindowInsetsListener(settingsMain) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navigationBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            view.updatePadding(
                top = statusBar.top,
                bottom = navigationBar.bottom
            )
            insets
        }

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareTextView = findViewById<MaterialTextView>(R.id.shareTextView)
        val supportTextView = findViewById<MaterialTextView>(R.id.supportTextView)
        val termsTextView = findViewById<MaterialTextView>(R.id.termsTextView)
        darkThemeSwitch = findViewById(R.id.darkThemeSwitch)

        shareTextView.setOnClickListener {
            viewModel.shareApp()
        }

        supportTextView.setOnClickListener {
            viewModel.writeToSupport()
        }

        termsTextView.setOnClickListener {
            viewModel.openTermsOfUse()
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            if (!isUpdatingFromCode) {
                viewModel.onThemeChanged(checked)
                delegate.applyDayNight()
            }
        }

        viewModel.themeState.observe(this) { isDarkTheme ->
            isUpdatingFromCode = true
            darkThemeSwitch.isChecked = isDarkTheme
            isUpdatingFromCode = false
        }

        viewModel.shouldRecreate.observe(this) { shouldRecreate ->
            if (shouldRecreate) {
                recreate()
                viewModel.onRecreated()
            }
        }
    }
}