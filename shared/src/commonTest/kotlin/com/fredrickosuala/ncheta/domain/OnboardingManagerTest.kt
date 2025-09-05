package com.fredrickosuala.ncheta.domain

import com.fredrickosuala.ncheta.TestSettings
import com.fredrickosuala.ncheta.TestSettingsRepository
import com.fredrickosuala.ncheta.domain.onboarding.OnboardingManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class OnboardingManagerTest {

    @Test
    fun `hasCompletedOnboarding should be false initially`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        
        // Act
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)
        val hasCompleted = onboardingManager.hasCompletedOnboarding.first()

        // Assert
        assertFalse(hasCompleted, "The initial onboarding state should be false.")
        assertEquals(false, testSettings.getBoolean("onboarding_complete", false))
    }

    @Test
    fun `setOnboardingCompleted should update settings and internal state`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)

        // Act
        onboardingManager.setOnboardingCompleted()

        // Assert
        assertTrue(onboardingManager.hasCompletedOnboarding.first())
        assertTrue(testSettings.getBoolean("onboarding_complete", false))
        assertEquals(true, testSettings.getStorage()["onboarding_complete"])
    }

    @Test
    fun `isReadyToUseApp should return true when both conditions are met`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        testSettings.putBoolean("onboarding_complete", true)
        testSettingsRepository.setApiKey("valid-api-key")
        
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)

        // Act
        val isReady = onboardingManager.isReadyToUseApp.first()

        // Assert
        assertTrue(isReady, "App should be ready when onboarding is complete and API key is present")
    }

    @Test
    fun `isReadyToUseApp should return false when onboarding is incomplete`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        testSettings.putBoolean("onboarding_complete", false)
        testSettingsRepository.setApiKey("valid-api-key")
        
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)

        // Act
        val isReady = onboardingManager.isReadyToUseApp.first()

        // Assert
        assertFalse(isReady, "App should not be ready when onboarding is incomplete")
    }

    @Test
    fun `isReadyToUseApp should return false when API key is missing`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        testSettings.putBoolean("onboarding_complete", true)
        testSettingsRepository.setApiKey(null)
        
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)

        // Act
        val isReady = onboardingManager.isReadyToUseApp.first()

        // Assert
        assertFalse(isReady, "App should not be ready when API key is missing")
    }

    @Test
    fun `isReadyToUseApp should return false when API key is blank`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        testSettings.putBoolean("onboarding_complete", true)
        testSettingsRepository.setApiKey("")
        
        val onboardingManager = OnboardingManager(testSettings, testSettingsRepository)

        // Act
        val isReady = onboardingManager.isReadyToUseApp.first()

        // Assert
        assertFalse(isReady, "App should not be ready when API key is blank")
    }

    @Test
    fun `onboarding state should persist across manager instances`() = runTest {
        // Arrange
        val testSettings = TestSettings()
        val testSettingsRepository = TestSettingsRepository()
        
        // First instance
        val onboardingManager1 = OnboardingManager(testSettings, testSettingsRepository)
        onboardingManager1.setOnboardingCompleted()
        
        // Second instance with same settings
        val onboardingManager2 = OnboardingManager(testSettings, testSettingsRepository)

        // Assert
        assertTrue(onboardingManager1.hasCompletedOnboarding.first())
        assertTrue(onboardingManager2.hasCompletedOnboarding.first())
        assertTrue(testSettings.getBoolean("onboarding_complete", false))
    }
}