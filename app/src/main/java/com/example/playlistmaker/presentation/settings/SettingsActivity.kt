package com.example.playlistmaker.presentation.settings

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {

    private lateinit var darkThemeSwitch: SwitchMaterial

    private val getThemeSettingsInteractor by lazy { Creator.provideGetThemeSettingsInteractor() }
    private val saveThemeSettingsInteractor by lazy { Creator.provideSaveThemeSettingsInteractor() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

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

        darkThemeSwitch.isChecked = getThemeSettingsInteractor.execute()

        darkThemeSwitch.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            saveThemeSettingsInteractor.execute(checked)
        }

        shareTextView.setOnClickListener {
            shareApp()
        }

        supportTextView.setOnClickListener {
            writeToSupport()
        }

        termsTextView.setOnClickListener {
            openTermsOfUse()
        }
    }

    private fun shareApp() {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun writeToSupport() {
        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email_address)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_email_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.support_email_text))
        }
        startActivity(emailIntent)
    }

    private fun openTermsOfUse() {
        val termsUrl = getString(R.string.terms_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, termsUrl.toUri())
        startActivity(browserIntent)
    }
}
