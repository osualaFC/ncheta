package com.fredrickosuala.ncheta.features.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.OnboardingManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val onboardingManager: OnboardingManager
) : ViewModel() {

    val hasCompletedOnboarding: StateFlow<Boolean?> =
        onboardingManager.hasCompletedOnboarding
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5000),
                initialValue = null
            )

    val isReadyToUseApp: StateFlow<Boolean> =
        onboardingManager.isReadyToUseApp
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Companion.WhileSubscribed(5000),
                initialValue = false
            )

    fun setOnboardingComplete() {
        viewModelScope.launch {
            onboardingManager.setOnboardingCompleted()
        }
    }
}