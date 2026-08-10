package com.zarnth.savr.data.local.mapper

import com.zarnth.savr.data.local.entity.CrashLogEntity
import com.zarnth.savr.domain.model.CrashLog

fun CrashLogEntity.toDomain(): CrashLog {
    return CrashLog(
        id = id,
        timestamp = timestamp,
        versionName = versionName,
        versionCode = versionCode,
        androidVersion = androidVersion,
        sdkInt = sdkInt,
        manufacturer = manufacturer,
        model = model,
        brand = brand,
        exceptionClass = exceptionClass,
        message = message,
        stackTrace = stackTrace,
        threadName = threadName
    )
}