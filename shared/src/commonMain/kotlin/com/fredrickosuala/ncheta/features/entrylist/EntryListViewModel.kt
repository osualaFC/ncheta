package com.fredrickosuala.ncheta.features.entrylist

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.repository.NchetaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EntryListViewModel(
    private val coroutineScope: CoroutineScope,
    private val repository: NchetaRepository
) {

    val entries: StateFlow<List<NchetaEntry>> = repository.getAllEntries()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteEntry(entryId: String) {
        coroutineScope.launch {
            repository.deleteEntryById(entryId)
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}