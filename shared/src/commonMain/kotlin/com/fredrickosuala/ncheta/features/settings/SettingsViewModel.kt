package com.fredrickosuala.ncheta.features.settings

import com.fredrickosuala.ncheta.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val coroutineScope: CoroutineScope
) {

    private val _apiKey = MutableStateFlow("")
    val apiKey = _apiKey.asStateFlow()

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        coroutineScope.launch {
            _apiKey.value = settingsRepository.getApiKey().first() ?: ""
        }
    }

    fun onApiKeyChanged(newKey: String) {
        _apiKey.value = newKey
    }

    fun saveApiKey() {
        _uiState.value = SettingsUiState.Saving
        coroutineScope.launch {
            settingsRepository.saveApiKey(apiKey.value)
            _uiState.value = SettingsUiState.Success
        }
    }

    fun resetUiState() {
        _uiState.value = SettingsUiState.Idle
    }

    fun clear() {
        coroutineScope.cancel()
    }
}



sealed class SettingsUiState {
    data object Idle : SettingsUiState()
    data object Saving : SettingsUiState()
    data object Success : SettingsUiState()
}