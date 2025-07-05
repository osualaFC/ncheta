package com.fredrickosuala.ncheta.features.entrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.NchetaRepository

class AndroidEntryListViewModel(
    repository: NchetaRepository
) : ViewModel() {

    val entryListViewModel = EntryListViewModel(
        coroutineScope = viewModelScope,
        repository = repository
    )

    override fun onCleared() {
        super.onCleared()
        entryListViewModel.clear()
    }
}