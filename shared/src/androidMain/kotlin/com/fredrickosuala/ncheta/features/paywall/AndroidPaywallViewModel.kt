package com.fredrickosuala.ncheta.features.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.config.RemoteConfigManager
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager

class AndroidPaywallViewModel(
    subscriptionManager: SubscriptionManager,
    remoteConfigManager: RemoteConfigManager
) : ViewModel() {

    val payWallViewModel = PaywallViewModel(
        coroutineScope = viewModelScope,
        subscriptionManager = subscriptionManager,
        remoteConfigManager = remoteConfigManager
    )

    override fun onCleared() {
        super.onCleared()
        payWallViewModel.clear()
    }
}