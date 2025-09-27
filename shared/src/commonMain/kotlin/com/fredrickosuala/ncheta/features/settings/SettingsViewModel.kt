package com.fredrickosuala.ncheta.features.settings

import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaUser
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.revenuecat.purchases.kmp.Purchases
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
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
            while (isActive) {
                _isPremium.value = subscriptionManager.getCustomerInfo().let {
                    it.entitlements["premium"]?.isActive == true && user.value?.uid == Purchases.sharedInstance.appUserID
                }
                delay(5000)
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
            _uiState.value = SettingsUiState.Success("API Key saved successfully!")
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

    fun restoreSubscription() {
        coroutineScope.launch {
           val wasRestored = subscriptionManager.restorePurchases()
            if (wasRestored.isSuccess) {
                _uiState.value = SettingsUiState.Success("Subscription restored successfully!")
            } else {
                _uiState.value = SettingsUiState.Error("Failed to restore subscription.")
            }
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}



sealed class SettingsUiState {
    data object Idle : SettingsUiState()
    data object Saving : SettingsUiState()
    data class Success(val message: String? = null) : SettingsUiState()
    data class Error(val message: String) : SettingsUiState()
}