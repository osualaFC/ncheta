package com.fredrickosuala.ncheta.domain.subscription

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow

class RevenueCatSubscriptionManager : SubscriptionManager {

    companion object {
        // This is the Entitlement ID you created in your RevenueCat dashboard
        private const val PREMIUM_ENTITLEMENT_ID = "premium"
        private const val POLL_INTERVAL_MS = 5_000L
    }

    override fun isPremium(): Flow<Boolean> = flow {
        var lastValue: Boolean? = null
        while (true) {
            try {
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                val isActive =
                    customerInfo.entitlements.active[PREMIUM_ENTITLEMENT_ID]?.isActive == true

                if (lastValue != isActive) {
                    emit(isActive)
                    lastValue = isActive
                }
            } catch (e: Exception) {
                if (lastValue != false) {
                    emit(false)
                    lastValue = false
                }
            }

            delay(POLL_INTERVAL_MS)
        }
    }.distinctUntilChanged()

    override suspend fun getOfferings(): Result<List<Offering>> {
      return try {
          val offeringsList = Purchases.sharedInstance.awaitOfferings().all.values.toList()
          Result.success(offeringsList)
      } catch (e: Exception) {
          Result.failure(e)
      }
    }

    override suspend fun purchase(pkg: Package): PurchaseResult {
        TODO("Not yet implemented")
    }

    override suspend fun restorePurchases(): Result<Boolean> {
        return try {
            val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
            val wasSuccess = customerInfo.entitlements.all[PREMIUM_ENTITLEMENT_ID]?.isActive == true
            Result.success(wasSuccess)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logIn(userId: String): Result<Boolean> = try {
        val logInResult = Purchases.sharedInstance.awaitLogIn(userId)
        restorePurchases()
        Result.success(logInResult.created)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun logOut(): Result<Boolean> = try {
        Purchases.sharedInstance.awaitLogOut()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }
}