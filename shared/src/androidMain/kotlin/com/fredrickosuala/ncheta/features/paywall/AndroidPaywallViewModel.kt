package com.fredrickosuala.ncheta.features.paywall

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.SubscriptionManager

class AndroidPaywallViewModel(
    subscriptionManager: SubscriptionManager,
) : ViewModel() {

    val payWallViewModel = PaywallViewModel(
        coroutineScope = viewModelScope,
        subscriptionManager = subscriptionManager
    )

    override fun onCleared() {
        super.onCleared()
        payWallViewModel.clear()
    }
}