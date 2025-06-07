package com.fredrickosuala.ncheta.features.input

import com.fredrickosuala.ncheta.features.util.UiState
import com.fredrickosuala.ncheta.services.Result
import com.fredrickosuala.ncheta.services.ContentGenerationService
import com.fredrickosuala.ncheta.services.GeminiContentGenerationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputViewModel(
    private val coroutineScope: CoroutineScope,
    private val generationService: ContentGenerationService
) {
    constructor() : this(
        coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
        generationService = GeminiContentGenerationService()
    )
    private val _uiState = MutableStateFlow<UiState<Any>>(UiState.Idle)
    val uiState: StateFlow<UiState<Any>> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()


    private var userApiKey: String? = "AIzaSyAGQujn9TJoQTf38EKeg7ZBUksoSgqKY_4" // Placeholder for User Story 2.1

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun onSummarizeClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = UiState.Loading
            when(val result = generationService.generateSummary(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun onGenerateFlashcardsClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = UiState.Loading
            when(val result = generationService.generateFlashcards(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun onGenerateQaClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = UiState.Loading
            when(val result = generationService.generateMcqs(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun resetUiState() {
        _uiState.value = UiState.Idle
    }

    // Placeholder for API key update logic (User Story 2.1)
    fun updateUserApiKey(apiKey: String?) {
        this.userApiKey = apiKey
        println("InputViewModel: API Key updated to: $apiKey")
    }

    private fun validateInputs(): Boolean {
        if (userApiKey.isNullOrBlank()) {
            _uiState.value = UiState.Error("API Key is missing. Please add it in settings.")
            return false
        }
        if (inputText.value.isBlank()) {
            _uiState.value = UiState.Error("Input text cannot be empty.")
            return false
        }
        return true
    }

    fun clear() {
        coroutineScope.cancel()
    }
}
