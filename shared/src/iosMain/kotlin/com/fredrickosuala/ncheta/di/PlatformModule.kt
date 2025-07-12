package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
import com.fredrickosuala.ncheta.features.auth.AuthViewModel
import com.fredrickosuala.ncheta.features.entrylist.EntryListViewModel
import com.fredrickosuala.ncheta.features.input.InputViewModel
import com.fredrickosuala.ncheta.features.practice.PracticeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single { DatabaseDriverFactory() }

    factory { CoroutineScope(Dispatchers.Main + SupervisorJob()) }

    factory {
        InputViewModel(
            generationService = get(),
            repository = get(),
            coroutineScope = get(),
            authRepository = get()
        )
    }

    factory {
        EntryListViewModel(
            repository = get(),
            coroutineScope = get()
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

}