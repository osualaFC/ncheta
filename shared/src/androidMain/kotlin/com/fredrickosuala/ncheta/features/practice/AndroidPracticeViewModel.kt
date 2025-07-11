package com.fredrickosuala.ncheta.features.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.repository.NchetaRepository

class AndroidPracticeViewModel(
    repository: NchetaRepository
) : ViewModel() {

    val practiceViewModel = PracticeViewModel(
        repository = repository,
        coroutineScope = viewModelScope
    )

    override fun onCleared() {
        super.onCleared()
        practiceViewModel.clear()
    }
}