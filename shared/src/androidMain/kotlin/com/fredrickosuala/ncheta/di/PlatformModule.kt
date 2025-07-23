package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
import com.fredrickosuala.ncheta.features.auth.AndroidAuthViewModel
import com.fredrickosuala.ncheta.features.entrylist.AndroidEntryListViewModel
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.main.MainViewModel
import com.fredrickosuala.ncheta.features.practice.AndroidPracticeViewModel
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
       AndroidInputViewModel(
           generationService = get(),
           repository = get(),
           authRepository = get()
       )
   }

    viewModel {
        AndroidEntryListViewModel(
            repository = get()
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
}