package com.fredrickosuala.ncheta.domain


import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class OnboardingManager(private val settings: Settings) {

    companion object {
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    private val _hasCompletedOnboarding = MutableStateFlow(
        settings.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    )

    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    fun setOnboardingCompleted() {
        settings.putBoolean(KEY_ONBOARDING_COMPLETE, true)
        _hasCompletedOnboarding.value = true
    }
}