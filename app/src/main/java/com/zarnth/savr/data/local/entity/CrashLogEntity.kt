package com.zarnth.savr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crash_logs")
data class CrashLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
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