package com.fredrickosuala.ncheta.features.entrylist

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.repository.NchetaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EntryListViewModel(
    private val coroutineScope: CoroutineScope,
    repository: NchetaRepository
) {

    val entries: StateFlow<List<NchetaEntry>> = repository.getAllEntries()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun clear() {
        coroutineScope.cancel()
    }
}