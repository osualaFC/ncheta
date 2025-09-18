package com.fredrickosuala.ncheta.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.SettingsRepository

class AndroidSettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository,
    private val subscriptionManager: SubscriptionManager
) : ViewModel() {

    val settingsViewModel = SettingsViewModel(
        settingsRepository = settingsRepository,
        authRepository = authRepository,
        subscriptionManager = subscriptionManager,
        coroutineScope = viewModelScope
    )

    override fun onCleared() {
        super.onCleared()
        settingsViewModel.clear()
    }
}