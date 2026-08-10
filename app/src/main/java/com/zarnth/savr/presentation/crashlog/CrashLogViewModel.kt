package com.zarnth.savr.presentation.crashlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zarnth.savr.domain.repository.CrashLogRepository
import com.zarnth.savr.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrashLogViewModel(
    private val repository: CrashLogRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CrashLogState())
    val state = _state.asStateFlow()

    init {
        loadCrashes()
    }

    fun onEvent(event: CrashLogEvents) {
        when (event) {
            is CrashLogEvents.SelectCrash -> {
                _state.update { it.copy(selectedCrash = event.crash) }
            }
            CrashLogEvents.DismissDetail -> {
                _state.update { it.copy(selectedCrash = null) }
            }
            CrashLogEvents.ShowClearDialog -> {
                _state.update { it.copy(showClearDialog = true) }
            }
            CrashLogEvents.HideClearDialog -> {
                _state.update { it.copy(showClearDialog = false) }
            }
            CrashLogEvents.ConfirmClear -> {
                clearAll()
            }
        }
    }

    private fun loadCrashes() {
        viewModelScope.launch {
            repository.observeCrashes().collect { resource ->
                when (resource) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true) }
                    is Resource.Error -> _state.update { it.copy(isLoading = false) }
                    is Resource.Success -> _state.update {
                        it.copy(isLoading = false, crashes = resource.data.orEmpty())
                    }
                }
            }
        }
    }

    private fun clearAll() {
        viewModelScope.launch {
            repository.clearAll()
            _state.update { it.copy(showClearDialog = false, selectedCrash = null) }
        }
    }
}