package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.Database
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.FirebaseAuthRepositoryImpl
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.NchetaRepositoryImpl
import com.fredrickosuala.ncheta.services.ContentGenerationService
import com.fredrickosuala.ncheta.services.GeminiContentGenerationService
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module


val sharedModule = module {

    single<ContentGenerationService> { GeminiContentGenerationService() }
    single { Database(databaseDriverFactory = get()) }
    single<NchetaRepository> { NchetaRepositoryImpl(database = get()) }
    single<AuthRepository> { FirebaseAuthRepositoryImpl() }

}

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(sharedModule, platformModule())
    }
}


expect fun platformModule(): Module