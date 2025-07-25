package com.fredrickosuala.ncheta.di

import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredrickosuala.ncheta.database.Database
import com.fredrickosuala.ncheta.domain.OnboardingManager
import com.fredrickosuala.ncheta.features.settings.SettingsViewModel
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.FirebaseAuthRepositoryImpl
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.NchetaRepositoryImpl
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.repository.SettingsRepositoryImpl
import com.fredrickosuala.ncheta.services.ContentGenerationService
import com.fredrickosuala.ncheta.services.GeminiContentGenerationService
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val sharedModule = module {

    single<ContentGenerationService> { GeminiContentGenerationService() }
    single { Database(databaseDriverFactory = get()) }
    single<NchetaRepository> { NchetaRepositoryImpl(database = get()) }
    single<AuthRepository> { FirebaseAuthRepositoryImpl() }
    single { OnboardingManager(settings = get(), settingsRepository = get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(settings = get()) }


}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModule, platformModule())
    }
}


expect fun platformModule(): Module