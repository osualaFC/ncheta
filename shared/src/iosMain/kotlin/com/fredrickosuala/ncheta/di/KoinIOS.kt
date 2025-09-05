package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.domain.onboarding.OnboardingManager
import com.fredrickosuala.ncheta.features.auth.AuthViewModel
import com.fredrickosuala.ncheta.features.entrylist.EntryListViewModel
import com.fredrickosuala.ncheta.features.input.InputViewModel
import com.fredrickosuala.ncheta.features.practice.PracticeViewModel
import com.fredrickosuala.ncheta.features.settings.SettingsViewModel
import com.fredrickosuala.ncheta.features.paywall.PaywallViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin() {
    startKoin {
        modules(sharedModule, platformModule(), audioRecorderModule())
    }
}

// Helper class to inject dependencies into Swift code
class ViewModels : KoinComponent {
    val inputViewModel: InputViewModel by inject()
    val entryListViewModel: EntryListViewModel by inject()
    val practiceViewModel: PracticeViewModel by inject()
    val authViewModel: AuthViewModel by inject()
    val onboardingManager: OnboardingManager by inject()
    val settingsViewModel: SettingsViewModel by inject()
    val paywallViewModel: PaywallViewModel by inject()
}

