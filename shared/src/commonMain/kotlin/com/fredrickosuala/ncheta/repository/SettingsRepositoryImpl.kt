package com.fredrickosuala.ncheta.repository

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val settings: Settings
) : SettingsRepository {

    companion object {
        private const val KEY_API_KEY = "gemini_api_key"
    }

    private val _apiKey = MutableStateFlow(
        settings.getString(KEY_API_KEY, "")
    )

    override suspend fun saveApiKey(key: String) {
        settings.putString(KEY_API_KEY, key)
        _apiKey.value = key
    }

    override fun getApiKey(): StateFlow<String?> {
        return _apiKey.asStateFlow()
    }
}