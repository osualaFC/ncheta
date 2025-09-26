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

    fun getPremiumCharLimit(): Int {
        return remoteConfig.get<Long>("premium_char_limit").toInt()
    }

    fun getFreeCharLimit(): Int {
        return remoteConfig.get<Long>("free_char_limit").toInt()
    }

    fun getFreeMaxGenerationLimit(): Int {
        return remoteConfig.get<Long>("free_daily_generations").toInt()
    }

    fun getApiKey(): String {
        return remoteConfig["api_key"]
    }

    fun getGeminiModel(): String {
        return remoteConfig["gemini_model"]
    }

}