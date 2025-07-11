package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
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

    factory {
        InputViewModel(
            generationService = get(),
            repository = get(),
            coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        )
    }

    factory {
        EntryListViewModel(
            repository = get(),
            coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        )
    }

    factory {
        PracticeViewModel(
            repository = get(),
            coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
        )
    }
}