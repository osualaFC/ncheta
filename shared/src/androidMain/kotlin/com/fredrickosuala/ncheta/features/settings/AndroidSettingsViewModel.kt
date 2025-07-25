package com.fredrickosuala.ncheta.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.SettingsRepository

class AndroidSettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val settingsViewModel = SettingsViewModel(
        settingsRepository = settingsRepository,
        coroutineScope = viewModelScope
    )

    override fun onCleared() {
        super.onCleared()
        settingsViewModel.clear()
    }
}