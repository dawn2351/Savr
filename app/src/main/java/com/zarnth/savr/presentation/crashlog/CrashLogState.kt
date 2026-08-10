package com.zarnth.savr.presentation.crashlog

import com.zarnth.savr.domain.model.CrashLog

data class CrashLogState(
    val isLoading: Boolean = false,
    val crashes: List<CrashLog> = emptyList(),
    val selectedCrash: CrashLog? = null,
    val showClearDialog: Boolean = false
)