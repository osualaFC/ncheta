package com.fredrickosuala.ncheta.repository


import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    /**
     * Saves the user's Gemini API key.
     */
    suspend fun saveApiKey(key: String)

    /**
     * Retrieves the user's Gemini API key as a Flow, which will emit
     * new values if the key is ever updated.
     */
    fun  getApiKey(): StateFlow<String?>
}