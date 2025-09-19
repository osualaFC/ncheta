package com.fredrickosuala.ncheta.features.settings

import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaUser
import com.fredrickosuala.ncheta.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val subscriptionManager: SubscriptionManager,
    private val coroutineScope: CoroutineScope
) {

    private val _apiKey = MutableStateFlow("")
    val apiKey = _apiKey.asStateFlow()

    private val _uiState = MutableStateFlow<SettingsUiState>(SettingsUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    val user: StateFlow<NchetaUser?> = authRepository.observeAuthState()
        .map { isLoggedIn -> if (isLoggedIn) authRepository.getCurrentUser() else null }
        .stateIn(coroutineScope, SharingStarted.WhileSubscribed(5000), null)


    init {
        coroutineScope.launch {
            _apiKey.value = settingsRepository.getApiKey().first() ?: ""
        }
        coroutineScope.launch {
            _isPremium.value = subscriptionManager.getCustomerInfo().let {
                it.entitlements["premium"]?.isActive == true
            }
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

    fun signOut() {
        coroutineScope.launch {
            authRepository.signOut()
        }
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