package com.zarnth.savr.presentation.crashlog

import com.zarnth.savr.domain.model.CrashLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val crashTimeFormatter = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())

fun formatCrashTime(timestamp: Long): String {
    return crashTimeFormatter.format(Date(timestamp))
}

fun buildCrashReport(crash: CrashLog): String {
    return buildString {
        appendLine("--- Savr Crash Report ---")
        appendLine("Time: ${formatCrashTime(crash.timestamp)}")
        appendLine("App version: ${crash.versionName} (${crash.versionCode})")
        appendLine("Android version: ${crash.androidVersion} (API ${crash.sdkInt})")
        appendLine("Device: ${crash.manufacturer} ${crash.model}")
        appendLine("Thread: ${crash.threadName}")
        appendLine("Exception: ${crash.exceptionClass}")
        appendLine("Message: ${crash.message}")
        appendLine()
        append("Stack trace:\n")
        append(crash.stackTrace)
    }
}