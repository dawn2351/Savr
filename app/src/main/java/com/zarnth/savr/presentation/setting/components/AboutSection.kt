package com.zarnth.savr.presentation.setting.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zarnth.savr.BuildConfig
import com.zarnth.savr.R

@Composable
fun AboutSection(
    versionName: String,
    onOpenCrashLogs: () -> Unit = {},
    onTestCrash: () -> Unit = {}
) {
    Spacer(Modifier.height(12.dp))
    SectionHeader("About")
    SettingItem(
        icon = R.drawable.about_icon,
        title = "App version",
        subtitle = versionName,
        onClick = { }
    )
    Spacer(Modifier.height(4.dp))
    SettingItem(
        icon = R.drawable.bug_icon,
        title = "Crash logs",
        subtitle = "View saved crash details",
        onClick = onOpenCrashLogs
    )
    if (BuildConfig.DEBUG) {
        Spacer(Modifier.height(4.dp))
        SettingItem(
            icon = R.drawable.bug_icon,
            title = "Crash the app",
            subtitle = "Temporary — triggers a test crash",
            destructive = true,
            onClick = onTestCrash
        )
    }
}
