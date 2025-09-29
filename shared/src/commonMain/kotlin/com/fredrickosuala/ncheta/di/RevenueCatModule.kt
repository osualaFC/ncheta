package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.BuildKonfig
import com.revenuecat.purchases.kmp.LogLevel
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.PurchasesConfiguration

fun initializeRevenueCat() {
    val apiKey = if (isAndroid) BuildKonfig.ANDROID_KEY else BuildKonfig.IOS_KEY

    val configuration = PurchasesConfiguration.Builder(apiKey).build()
    Purchases.configure(configuration)
    Purchases.logLevel = LogLevel.DEBUG
}