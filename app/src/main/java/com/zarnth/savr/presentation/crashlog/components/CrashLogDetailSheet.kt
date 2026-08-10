package com.zarnth.savr.presentation.crashlog.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.zarnth.savr.domain.model.CrashLog
import com.zarnth.savr.presentation.crashlog.buildCrashReport
import com.zarnth.savr.presentation.crashlog.formatCrashTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrashLogDetailSheet(
    crash: CrashLog,
    onCopy: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
            Text(
                text = "Crash details",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = crash.exceptionClass,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            if (crash.message.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = crash.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(16.dp))
            InfoRow("Time", formatCrashTime(crash.timestamp))
            InfoRow("App version", "${crash.versionName} (${crash.versionCode})")
            InfoRow("Android version", "${crash.androidVersion} (API ${crash.sdkInt})")
            InfoRow("Device", "${crash.manufacturer} ${crash.model}")
            InfoRow("Thread", crash.threadName)
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Stack trace",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = crash.stackTrace,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onCopy,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Copy report")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(0.6f)
        )
    }
}