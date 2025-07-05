package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.features.entrylist.EntryListViewModel
import com.fredrickosuala.ncheta.features.input.InputViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin


fun initKoin() {
    startKoin {
        modules(sharedModule, platformModule())
    }
}

// Helper class to inject dependencies into Swift code
class ViewModels : KoinComponent {
    val inputViewModel: InputViewModel by inject()
    val entryListViewModel: EntryListViewModel by inject()
}

