package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.TestSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SettingsRepositoryTest {

    @Test
    fun `getApiKey should return empty string initially`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act
        val apiKey = settingsRepository.getApiKey().first()

        // Assert
        assertEquals("", apiKey)
        assertEquals("", testSettings.getString("gemini_api_key", ""))
    }

    @Test
    fun `saveApiKey should store and update internal state`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testApiKey = "test-api-key-123"
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act
        settingsRepository.saveApiKey(testApiKey)
        val storedApiKey = settingsRepository.getApiKey().first()

        // Assert
        assertEquals(testApiKey, storedApiKey)
        assertEquals(testApiKey, testSettings.getString("gemini_api_key", ""))
        assertEquals(testApiKey, testSettings.getStorage()["gemini_api_key"])
    }

    @Test
    fun `getApiKey should return stored value from settings`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val storedApiKey = "stored-api-key"
        testSettings.putString("gemini_api_key", storedApiKey)
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act
        val apiKey = settingsRepository.getApiKey().first()

        // Assert
        assertEquals(storedApiKey, apiKey)
        assertEquals(storedApiKey, testSettings.getString("gemini_api_key", ""))
    }

    @Test
    fun `saveApiKey should update existing value`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val initialApiKey = "initial-key"
        val newApiKey = "new-key"
        testSettings.putString("gemini_api_key", initialApiKey)
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act
        settingsRepository.saveApiKey(newApiKey)
        val updatedApiKey = settingsRepository.getApiKey().first()

        // Assert
        assertEquals(newApiKey, updatedApiKey)
        assertEquals(newApiKey, testSettings.getString("gemini_api_key", ""))
        assertEquals(newApiKey, testSettings.getStorage()["gemini_api_key"])
    }

    @Test
    fun `getApiKey should emit new values when updated`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val initialApiKey = "initial-key"
        val newApiKey = "new-key"
        testSettings.putString("gemini_api_key", initialApiKey)
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act & Assert
        assertEquals(initialApiKey, settingsRepository.getApiKey().first())
        
        settingsRepository.saveApiKey(newApiKey)
        assertEquals(newApiKey, settingsRepository.getApiKey().first())
    }

    @Test
    fun `saveApiKey should handle special characters and long keys`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val specialApiKey = "api-key-with-special-chars!@#$%^&*()_+-=[]{}|;':\",./<>?"
        val longApiKey = "a".repeat(1000)
        val settingsRepository = SettingsRepositoryImpl(testSettings)

        // Act & Assert for special characters
        settingsRepository.saveApiKey(specialApiKey)
        assertEquals(specialApiKey, settingsRepository.getApiKey().first())
        assertEquals(specialApiKey, testSettings.getStorage()["gemini_api_key"])

        // Act & Assert for long key
        settingsRepository.saveApiKey(longApiKey)
        assertEquals(longApiKey, settingsRepository.getApiKey().first())
        assertEquals(longApiKey, testSettings.getStorage()["gemini_api_key"])
    }

    @Test
    fun `settings should persist across repository instances`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testApiKey = "persistent-api-key"
        
        // First repository instance
        val repository1 = SettingsRepositoryImpl(testSettings)
        repository1.saveApiKey(testApiKey)
        
        // Second repository instance with same settings
        val repository2 = SettingsRepositoryImpl(testSettings)

        // Assert
        assertEquals(testApiKey, repository1.getApiKey().first())
        assertEquals(testApiKey, repository2.getApiKey().first())
        assertEquals(testApiKey, testSettings.getStorage()["gemini_api_key"])
    }
} 