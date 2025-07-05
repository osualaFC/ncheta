package com.fredrickosuala.ncheta.features.entrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.repository.NchetaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EntryListViewModel(
    repository: NchetaRepository
) : ViewModel() {

    val entries: StateFlow<List<NchetaEntry>> = repository.getAllEntries()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}