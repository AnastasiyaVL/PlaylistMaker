package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow_24)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val shareLayout = findViewById<LinearLayout>(R.id.shareLayout)
        val supportLayout = findViewById<LinearLayout>(R.id.supportLayout)
        val termsLayout = findViewById<LinearLayout>(R.id.termsLayout)


        shareLayout.setOnClickListener {
            shareApp()
        }

        supportLayout.setOnClickListener {
            writeToSupport()
        }

        termsLayout.setOnClickListener {
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
            data = "mailto:".toUri() // Только mailto, без адреса
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