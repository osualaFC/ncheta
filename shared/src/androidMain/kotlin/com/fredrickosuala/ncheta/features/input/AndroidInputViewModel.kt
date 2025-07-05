package com.fredrickosuala.ncheta.features.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.services.ContentGenerationService

class AndroidInputViewModel(
    generationService: ContentGenerationService,
    repository: NchetaRepository
) : ViewModel() {

    val inputViewModel = InputViewModel(
        coroutineScope = viewModelScope,
        generationService = generationService,
        repository = repository
    )

    override fun onCleared() {
        super.onCleared()
        inputViewModel.clear()
    }

}