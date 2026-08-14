package com.zarnth.savr

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import com.zarnth.savr.domain.repository.SettingsRepository
import com.zarnth.savr.presentation.root.RootScreen
import com.zarnth.savr.ui.theme.ThemeMode
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        applyLaunchWindowTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedUrl = when (intent?.action) {
            Intent.ACTION_SEND -> intent.getStringExtra(Intent.EXTRA_TEXT)?.trim()
            else -> null
        }

        setContent {
            RootScreen(sharedUrl = sharedUrl)
        }
    }

    private fun applyLaunchWindowTheme() {
        when (settingsRepository.getThemeMode()) {
            ThemeMode.AMOLED -> {
                setTheme(R.style.Theme_Savr_Dark)
                window.decorView.setBackgroundColor(android.graphics.Color.BLACK)
            }

            ThemeMode.DARK -> setTheme(R.style.Theme_Savr_Dark)
            ThemeMode.LIGHT -> setTheme(R.style.Theme_Savr_Light)
            ThemeMode.SYSTEM -> Unit // values(-night)/themes.xml already matches the system theme
        }
    }
}

fun openChromeTab(url: String, context: Context) {
    val intent = CustomTabsIntent.Builder().build()
    intent.launchUrl(context, url.toUri())
}
