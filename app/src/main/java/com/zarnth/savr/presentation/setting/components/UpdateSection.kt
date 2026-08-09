package com.zarnth.savr.presentation.setting.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zarnth.savr.R
import com.zarnth.savr.presentation.setting.SettingEvents
import com.zarnth.savr.presentation.setting.SettingState
import com.zarnth.savr.presentation.setting.SettingViewModel
import com.zarnth.savr.presentation.setting.UpdateState

@Composable
fun UpdateSection(
    state: SettingState,
    viewModel: SettingViewModel,
    versionName: String
) {
    Spacer(Modifier.height(12.dp))
    SectionHeader("Update")

    val updateState = state.updateState
    when (updateState) {
        is UpdateState.Idle -> SettingItem(
            icon = R.drawable.update_icon,
            title = "App update",
            subtitle = "Version $versionName",
            onClick = { viewModel.onEvent(SettingEvents.CheckForUpdate) }
        )

        is UpdateState.Checking -> SettingItem(
            icon = R.drawable.update_icon,
            title = "Checking for updates…",
            subtitle = "Looking for the latest version",
            trailing = {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = { }
        )

        is UpdateState.UpToDate -> SettingItem(
            icon = R.drawable.update_icon,
            title = "You're up to date",
            subtitle = "Version $versionName",
            onClick = { viewModel.onEvent(SettingEvents.CheckForUpdate) }
        )

        is UpdateState.Available -> SettingItem(
            icon = R.drawable.update_icon,
            title = "Update available",
            subtitle = "Version ${updateState.latestVersion} is ready",
            onClick = { viewModel.onEvent(SettingEvents.ShowUpdateSheet) }
        )

        is UpdateState.Downloading -> SettingItem(
            icon = R.drawable.update_icon,
            title = "Downloading update…",
            subtitle = "Please wait",
            trailing = {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            },
            onClick = { }
        )

        is UpdateState.ReadyToInstall -> SettingItem(
            icon = R.drawable.update_icon,
            title = "Update ready",
            subtitle = "Tap to install",
            onClick = { }
        )

        is UpdateState.DownloadFailed -> SettingItem(
            icon = R.drawable.update_icon,
            title = "Download failed",
            subtitle = "Tap to try again",
            onClick = { viewModel.onEvent(SettingEvents.CheckForUpdate) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateSheet(
    latestVersion: String,
    notes: String,
    onDownload: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(
                text = "Update available",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "A new version of Savr is ready to install.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Version $latestVersion",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            if (notes.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onDownload,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Download & install")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Not now")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
