package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
import com.fredrickosuala.ncheta.features.auth.AndroidAuthViewModel
import com.fredrickosuala.ncheta.features.entrylist.AndroidEntryListViewModel
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.main.MainViewModel
import com.fredrickosuala.ncheta.features.paywall.AndroidPaywallViewModel
import com.fredrickosuala.ncheta.features.practice.AndroidPracticeViewModel
import com.fredrickosuala.ncheta.features.settings.AndroidSettingsViewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single { DatabaseDriverFactory(androidContext()) }

    single<Settings> {
        SharedPreferencesSettings.Factory(androidContext()).create(name = "ncheta_settings")
    }

    viewModel { MainViewModel(onboardingManager = get()) }

    viewModel {
        AndroidSettingsViewModel(
            settingsRepository = get(),
            authRepository = get(),
            subscriptionManager = get()
        )
    }

   viewModel {
       AndroidInputViewModel(
           generationService = get(),
           repository = get(),
           authRepository = get(),
           settingsRepository = get(),
           audioRecorder = get(),
           subscriptionManager = get()
       )
   }

    viewModel {
        AndroidEntryListViewModel(
            repository = get(),
            subscriptionManager = get()
        )
    }

    viewModel {
        AndroidPracticeViewModel(
            repository = get()
        )
    }

    viewModel {
        AndroidAuthViewModel(
            authRepository = get()
        )
    }

    viewModel {
        AndroidPaywallViewModel(
            subscriptionManager = get()
        )
    }
}

internal actual val isAndroid: Boolean = true