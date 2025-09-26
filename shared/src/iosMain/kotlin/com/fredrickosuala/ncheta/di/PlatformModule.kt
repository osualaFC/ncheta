package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
import com.fredrickosuala.ncheta.features.auth.AuthViewModel
import com.fredrickosuala.ncheta.features.entrylist.EntryListViewModel
import com.fredrickosuala.ncheta.features.input.InputViewModel
import com.fredrickosuala.ncheta.features.paywall.PaywallViewModel
import com.fredrickosuala.ncheta.features.practice.PracticeViewModel
import com.fredrickosuala.ncheta.features.settings.SettingsViewModel
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single { DatabaseDriverFactory() }

    single<Settings> { NSUserDefaultsSettings.Factory().create("ncheta_settings") }

    factory { CoroutineScope(Dispatchers.Main + SupervisorJob()) }

    factory {
        SettingsViewModel(
            settingsRepository = get(),
            authRepository = get(),
            subscriptionManager = get(),
            coroutineScope = get()
        )
    }

    factory {
        InputViewModel(
            generationService = get(),
            repository = get(),
            coroutineScope = get(),
            authRepository = get(),
            settingsRepository = get(),
            audioRecorder = get(),
            subscriptionManager = get(),
            remoteConfigManager = get()
        )
    }

    factory {
        EntryListViewModel(
            repository = get(),
            coroutineScope = get(),
            subscriptionManager = get(),
            authRepository = get()
        )
    }

    factory {
        PracticeViewModel(
            repository = get(),
            coroutineScope = get()
        )
    }

    factory {
        AuthViewModel(
            authRepository = get(),
            coroutineScope = get()
        )
    }

    factory {
        PaywallViewModel(
            subscriptionManager = get(),
            remoteConfigManager = get(),
            coroutineScope = get()
        )
    }

}

internal actual val isAndroid: Boolean = false