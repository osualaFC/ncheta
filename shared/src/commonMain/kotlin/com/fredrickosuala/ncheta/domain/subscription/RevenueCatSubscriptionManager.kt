package com.fredrickosuala.ncheta.domain.subscription

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.models.CustomerInfo
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

    override suspend fun getCustomerInfo(): CustomerInfo {
        return Purchases.sharedInstance.awaitCustomerInfo()
    }

    override fun isPremium(userId: String): Flow<Boolean> = flow {

        if (userId.isBlank()) {
            emit(false)
            return@flow
        }

        val loginResult = try {
            Purchases.sharedInstance.awaitLogIn(userId)
        } catch (e: Exception) {
            emit(false)
            return@flow
        }

        var lastValue: Boolean? = null

        while (true) {
            try {
                // Use getCustomerInfo instead of logging in again
                val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
                val isActive = customerInfo.entitlements[PREMIUM_ENTITLEMENT_ID]?.isActive == true

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