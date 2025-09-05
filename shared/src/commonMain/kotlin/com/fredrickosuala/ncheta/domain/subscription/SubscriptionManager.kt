package com.fredrickosuala.ncheta.domain.subscription


import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.flow.Flow

interface SubscriptionManager {
    /**
     * A flow that emits true if the user has an active "premium" entitlement.
     */
    fun isPremium(): Flow<Boolean>

    /**
     * Fetches the current offerings (e.g., "monthly", "annual" plans) from RevenueCat.
     */
    suspend fun getOfferings(): Result<List<Offering>>

    /**
     * Initiates the purchase flow for a specific package.
     * This is an expect function because the purchase UI is platform-specific.
     */
    suspend fun purchase(pkg: Package): PurchaseResult

    /**
     * Restores a user's previous purchases.
     */
    suspend fun restorePurchases(): Result<Boolean>
}

sealed class PurchaseResult {
    data class Success(val customerInfo: CustomerInfo) : PurchaseResult()
    data object UserCancelled : PurchaseResult()
    data class Error(val message: String) : PurchaseResult()
}