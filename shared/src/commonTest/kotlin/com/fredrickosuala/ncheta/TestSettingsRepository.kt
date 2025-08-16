package com.fredrickosuala.ncheta

import com.fredrickosuala.ncheta.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Test implementation of SettingsRepository for unit testing
 */
class TestSettingsRepository : SettingsRepository {
    private val _apiKey = MutableStateFlow<String?>(null)
    
    override suspend fun saveApiKey(key: String) {
        _apiKey.value = key
    }
    
    override fun getApiKey(): StateFlow<String?> {
        return _apiKey.asStateFlow()
    }
    
    // Helper method for testing
    fun setApiKey(key: String?) {
        _apiKey.value = key
    }
    
    fun getCurrentApiKey(): String? = _apiKey.value
} 