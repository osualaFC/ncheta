package com.fredrickosuala.ncheta.domain

import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine

class OnboardingManager(
    private val settings: Settings,
    settingsRepository: SettingsRepository
) {

    companion object {
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    private val _hasCompletedOnboarding = MutableStateFlow(
        settings.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    )

    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    /**
     * A flow that emits true only if BOTH onboarding is complete AND an API key is present.
     */
    val isReadyToUseApp: Flow<Boolean> =
        _hasCompletedOnboarding
            .combine(settingsRepository.getApiKey()) { isOnboardingComplete, apiKey ->
                isOnboardingComplete && !apiKey.isNullOrBlank()
            }

    fun setOnboardingCompleted() {
        settings.putBoolean(KEY_ONBOARDING_COMPLETE, true)
        _hasCompletedOnboarding.value = true
    }
}