package com.fredrickosuala.ncheta.domain

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RevenueCatSubscriptionManager : SubscriptionManager {

    companion object {
        // This is the Entitlement ID you created in your RevenueCat dashboard
        private const val PREMIUM_ENTITLEMENT_ID = "premium"
    }

    override suspend fun isPremium(): Flow<Boolean> = flow {
        val customerInfo = Purchases.sharedInstance.awaitCustomerInfo()
        emit(customerInfo.entitlements.active[PREMIUM_ENTITLEMENT_ID]?.isActive == true)
    }

    override suspend fun getOfferings(): Result<Offering> {
      return try {
          val offerings = Purchases.sharedInstance.awaitOfferings().current
          if (offerings != null) {
              Result.success(offerings)
          } else {
              Result.failure(Exception("No current offering found."))
          }
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
}