package com.zarnth.savr.domain.model

data class CrashLog(
    val id: Long,
    val timestamp: Long,
    val versionName: String,
    val versionCode: Long,
    val androidVersion: String,
    val sdkInt: Int,
    val manufacturer: String,
    val model: String,
    val brand: String,
    val exceptionClass: String,
    val message: String,
    val stackTrace: String,
    val threadName: String
)