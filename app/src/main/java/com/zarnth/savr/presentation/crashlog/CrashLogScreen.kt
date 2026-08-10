@file:OptIn(ExperimentalMaterial3Api::class)

package com.zarnth.savr.presentation.crashlog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.zarnth.savr.R
import com.zarnth.savr.presentation.crashlog.components.CrashLogCard
import com.zarnth.savr.presentation.crashlog.components.CrashLogDetailSheet
import com.zarnth.savr.presentation.home.components.LoadingProgress
import org.koin.androidx.compose.koinViewModel

@Composable
fun CrashLogScreen(
    viewModel: CrashLogViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val clipboard = LocalClipboard.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.crashes.isEmpty() && !state.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.bug_icon),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "No crashes logged",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "If the app ever stops unexpectedly, the crash details will show up here automatically.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.crashes.size == 1) "1 crash" else "${state.crashes.size} crashes",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    if (state.crashes.isNotEmpty()) {
                        TextButton(onClick = { viewModel.onEvent(CrashLogEvents.ShowClearDialog) }) {
                            Text("Clear all", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = state.crashes,
                        key = { it.id }
                    ) { crash ->
                        CrashLogCard(
                            crash = crash,
                            onClick = { viewModel.onEvent(CrashLogEvents.SelectCrash(crash)) }
                        )
                    }
                }
            }
        }

        LoadingProgress(state.isLoading)
    }

    state.selectedCrash?.let { crash ->
        CrashLogDetailSheet(
            crash = crash,
            onCopy = {
                clipboard.nativeClipboard.text = buildCrashReport(crash)
            },
            onDismiss = { viewModel.onEvent(CrashLogEvents.DismissDetail) }
        )
    }

    if (state.showClearDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CrashLogEvents.HideClearDialog) },
            title = { Text("Clear crash logs?") },
            text = { Text("This will permanently remove all saved crash reports from this device.") },
            confirmButton = {
                TextButton(onClick = { viewModel.onEvent(CrashLogEvents.ConfirmClear) }) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(CrashLogEvents.HideClearDialog) }) {
                    Text("Cancel")
                }
            }
        )
    }
}