package com.fredrickosuala.ncheta.features.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.services.ContentGenerationService

class AndroidInputViewModel(
    generationService: ContentGenerationService,
    repository: NchetaRepository,
    authRepository: AuthRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val inputViewModel = InputViewModel(
        coroutineScope = viewModelScope,
        generationService = generationService,
        repository = repository,
        authRepository = authRepository,
        settingsRepository = settingsRepository
    )

    override fun onCleared() {
        super.onCleared()
        inputViewModel.clear()
    }

}