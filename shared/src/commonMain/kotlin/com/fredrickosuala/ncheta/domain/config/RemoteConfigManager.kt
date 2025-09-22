package com.fredrickosuala.ncheta.domain.config

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.get
import dev.gitlive.firebase.remoteconfig.remoteConfig

class RemoteConfigManager() {
    private val remoteConfig = Firebase.remoteConfig

    suspend fun fetchAndActivate() {
        remoteConfig.setDefaults(("promo_code" to "FALLBACK_CODE"))
        remoteConfig.fetchAndActivate()
    }

    fun getPromoCode(): String {
        return remoteConfig["promo_code"]
    }
}