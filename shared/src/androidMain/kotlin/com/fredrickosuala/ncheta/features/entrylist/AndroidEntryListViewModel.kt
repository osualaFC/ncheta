package com.fredrickosuala.ncheta.features.entrylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.NchetaRepository

class AndroidEntryListViewModel(
    repository: NchetaRepository,
    subscriptionManager: SubscriptionManager
) : ViewModel() {

    val entryListViewModel = EntryListViewModel(
        coroutineScope = viewModelScope,
        repository = repository,
        subscriptionManager = subscriptionManager
    )

    override fun onCleared() {
        super.onCleared()
        entryListViewModel.clear()
    }
}