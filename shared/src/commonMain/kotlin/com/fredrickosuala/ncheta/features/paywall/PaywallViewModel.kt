package com.fredrickosuala.ncheta.features.paywall

import com.fredrickosuala.ncheta.domain.PurchaseResult
import com.fredrickosuala.ncheta.domain.SubscriptionManager
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaywallViewModel(
    private val subscriptionManager: SubscriptionManager,
    private val coroutineScope: CoroutineScope
) {

    private val _state = MutableStateFlow<PaywallState>(PaywallState.Loading)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PaywallEvent>()
    val events = _events.asSharedFlow()

    init {
        loadOfferings()
    }

    private fun loadOfferings() {
        coroutineScope.launch {
            _state.value = PaywallState.Loading
            subscriptionManager.getOfferings()
                .onSuccess { offering ->
                    _state.value = PaywallState.Success(offering)
                }
                .onFailure { error ->
                    _state.value = PaywallState.Error(error.message ?: "Could not load products.")
                }
        }
    }

    fun onPurchaseClicked(pkg: Package) {
        coroutineScope.launch {
            _events.emit(PaywallEvent.RequestPurchase(pkg))
        }
    }

    fun onPurchaseCompleted(purchaseResult: PurchaseResult) {
        println("Purchase result: $purchaseResult")
    }

    fun clear() {
        coroutineScope.cancel()
    }
}