package com.fredrickosuala.ncheta.di

import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration

object RevenueCatKeys {
    const val GOOGLE_API_KEY = "goog_VjazuZOxZZPHNHqgesYedpDWIwO"
    const val APPLE_API_KEY = "appl_XoFaallFNkeXvKStSYOQQKSJibc"
}

fun initializeRevenueCat() {
    val apiKey = if (isAndroid) RevenueCatKeys.GOOGLE_API_KEY else RevenueCatKeys.APPLE_API_KEY

    val configuration = PurchasesConfiguration.Builder(apiKey).build()
    Purchases.configure(configuration)
    Purchases.logLevel = LogLevel.DEBUG
}