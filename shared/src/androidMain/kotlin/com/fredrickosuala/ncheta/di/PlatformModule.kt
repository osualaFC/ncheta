package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.database.DatabaseDriverFactory
import com.fredrickosuala.ncheta.features.entrylist.AndroidEntryListViewModel
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.practice.AndroidPracticeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {

    single { DatabaseDriverFactory(androidContext()) }

   viewModel {
       AndroidInputViewModel(
           generationService = get(),
           repository = get()
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
}