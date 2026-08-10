package com.zarnth.savr.presentation.crashlog

import com.zarnth.savr.domain.model.CrashLog

sealed class CrashLogEvents {
    data class SelectCrash(val crash: CrashLog) : CrashLogEvents()
    object DismissDetail : CrashLogEvents()
    object ShowClearDialog : CrashLogEvents()
    object HideClearDialog : CrashLogEvents()
    object ConfirmClear : CrashLogEvents()
}