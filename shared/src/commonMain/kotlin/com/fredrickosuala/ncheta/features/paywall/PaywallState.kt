package com.fredrickosuala.ncheta.features.paywall

import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package


sealed class PaywallEvent {
    data class RequestPurchase(val pkg: Package) : PaywallEvent()
}


sealed class PaywallState {
    data object Loading : PaywallState()
    data class Error(val message: String) : PaywallState()
    data class Success(val offering: Offering) : PaywallState()
}