package com.fredrickosuala.ncheta.features.entrylist

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.NchetaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EntryListViewModel(
    private val coroutineScope: CoroutineScope,
    private val repository: NchetaRepository,
    private val subscriptionManager: SubscriptionManager
) {

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    fun syncEntries() {
        coroutineScope.launch {
            _isSyncing.value = true
            repository.syncRemoteEntries(isPremium())
            _isSyncing.value = false
        }
    }

    init {
        syncEntries()
    }

    val entries: StateFlow<List<NchetaEntry>> = repository.getAllEntries()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private suspend fun isPremium(): Boolean {
        return subscriptionManager.getCustomerInfo().let {
            it.entitlements["premium"]?.isActive == true
        }
    }

    fun deleteEntry(entryId: String) {
        coroutineScope.launch {
            repository.deleteEntryById(entryId)
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}