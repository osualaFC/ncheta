package com.fredrickosuala.ncheta.features.paywall

import com.fredrickosuala.ncheta.domain.config.RemoteConfigManager
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.revenuecat.purchases.kmp.models.Offering
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
    private val remoteConfigManager: RemoteConfigManager,
    private val coroutineScope: CoroutineScope
) {

    private val _state = MutableStateFlow<PaywallState>(PaywallState.Loading)
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<PaywallEvent>()
    val events = _events.asSharedFlow()

    private val _promoCode = MutableStateFlow("")
    val promoCode = _promoCode.asStateFlow()

    private var promoOffering: Offering? = null

    init {
        loadOfferings()
    }

    private fun loadOfferings() {
        coroutineScope.launch {
            _state.value = PaywallState.Loading
            subscriptionManager.getOfferings()
                .onSuccess { offering ->
                    promoOffering = offering.first { it.identifier == "promo" }
                    val otherOfferings = offering.filter { it.identifier != "promo" }
                    _state.value = PaywallState.Success(otherOfferings)
                }
                .onFailure { error ->
                    _state.value = PaywallState.Error(error.message ?: "Could not load products.")
                }
        }
    }

    fun onPromoCodeChanged(newCode: String) {
        _promoCode.value = newCode
    }


    fun applyPromoCode() {
        val validCode = remoteConfigManager.getPromoCode()

        if (promoCode.value.equals(validCode, ignoreCase = true) && validCode.isNotBlank()) {
            promoOffering?.availablePackages?.firstOrNull()?.let { promoPackage ->
                onPurchaseClicked(promoPackage)
            }
        } else {
            coroutineScope.launch {
                _events.emit(PaywallEvent.PromoError("Invalid promo code."))
            }
        }
    }

    fun onPurchaseClicked(pkg: Package) {
        coroutineScope.launch {
            _events.emit(PaywallEvent.RequestPurchase(pkg))
        }
    }

    fun onPurchaseSuccess() {
        println("Purchase result: Purchase completed")
    }

    fun clear() {
        coroutineScope.cancel()
    }
}