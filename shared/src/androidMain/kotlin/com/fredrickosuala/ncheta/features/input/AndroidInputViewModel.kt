package com.fredrickosuala.ncheta.features.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.domain.audio.AudioRecorder
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.services.ContentGenerationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

open class AndroidInputViewModel(
    generationService: ContentGenerationService,
    repository: NchetaRepository,
    authRepository: AuthRepository,
    settingsRepository: SettingsRepository,
    audioRecorder: AudioRecorder,
    subscriptionManager: SubscriptionManager
) : ViewModel() {

    val inputViewModel = InputViewModel(
        coroutineScope = viewModelScope,
        generationService = generationService,
        repository = repository,
        authRepository = authRepository,
        settingsRepository = settingsRepository,
        audioRecorder = audioRecorder,
        subscriptionManager = subscriptionManager
    )

    private val _saveDialogTitle = MutableStateFlow("")
    val saveDialogTitle = _saveDialogTitle.asStateFlow()

    fun onSaveDialogTitleChanged(newTitle: String) {
        _saveDialogTitle.value = newTitle
    }

    override fun onCleared() {
        super.onCleared()
        inputViewModel.clear()
    }

}