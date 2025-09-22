package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.Database
import com.fredrickosuala.ncheta.domain.config.RemoteConfigManager
import com.fredrickosuala.ncheta.domain.onboarding.OnboardingManager
import com.fredrickosuala.ncheta.domain.subscription.RevenueCatSubscriptionManager
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.FirebaseAuthRepositoryImpl
import com.fredrickosuala.ncheta.repository.FirestoreRemoteDataSource
import com.fredrickosuala.ncheta.repository.LocalDataSource
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.NchetaRepositoryImpl
import com.fredrickosuala.ncheta.repository.RemoteDataSource
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.repository.SettingsRepositoryImpl
import com.fredrickosuala.ncheta.repository.SqlDelightLocalDataSource
import com.fredrickosuala.ncheta.services.ContentGenerationService
import com.fredrickosuala.ncheta.services.GeminiContentGenerationService
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val sharedModule = module {

    single<ContentGenerationService> { GeminiContentGenerationService() }
    single { Database(databaseDriverFactory = get()) }
    single<LocalDataSource> { SqlDelightLocalDataSource(database = get()) }
    single<RemoteDataSource> { FirestoreRemoteDataSource() }
    single<SubscriptionManager> { RevenueCatSubscriptionManager() }
    single { RemoteConfigManager() }
    single<NchetaRepository> {
        NchetaRepositoryImpl(
            localDataSource = get(),
            remoteDataSource = get(),
            authRepository = get(),
            subscriptionManager = get()
        )
    }
    single<AuthRepository> { FirebaseAuthRepositoryImpl(subscriptionManager = get()) }
    single { OnboardingManager(settings = get(), settingsRepository = get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(settings = get()) }


}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModule, platformModule(), audioRecorderModule())
    }
}


expect fun platformModule(): Module

expect fun audioRecorderModule(): Module

internal expect val isAndroid: Boolean