package com.fredrickosuala.ncheta.domain.subscription

import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.ktx.awaitCustomerInfo
import com.revenuecat.purchases.kmp.ktx.awaitLogIn
import com.revenuecat.purchases.kmp.ktx.awaitLogOut
import com.revenuecat.purchases.kmp.ktx.awaitOfferings
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Offering
import com.revenuecat.purchases.kmp.models.Package

class RevenueCatSubscriptionManager : SubscriptionManager {

    companion object {
        // This is the Entitlement ID you created in your RevenueCat dashboard
        private const val PREMIUM_ENTITLEMENT_ID = "premium"
    }

    override suspend fun getCustomerInfo(): CustomerInfo {
        return Purchases.sharedInstance.awaitCustomerInfo()
    }

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