package com.fredrickosuala.ncheta.features.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fredrickosuala.ncheta.features.util.UiState
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.services.Result
import com.fredrickosuala.ncheta.services.ContentGenerationService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputViewModel(
    private val generationService: ContentGenerationService,
    private val repository: NchetaRepository
) : ViewModel() {

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

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when(val result = generationService.generateSummary(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun onGenerateFlashcardsClicked() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when(val result = generationService.generateFlashcards(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun onGenerateQaClicked() {
        if (!validateInputs()) return

        viewModelScope.launch {
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

    private fun generateContent(type: GenerationType) {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = when (type) {
                GenerationType.SUMMARY -> generationService.generateSummary(inputText.value, userApiKey.orEmpty())
                GenerationType.FLASHCARDS -> generationService.generateFlashcards(inputText.value, userApiKey.orEmpty())
                GenerationType.MCQS -> generationService.generateMcqs(inputText.value, userApiKey.orEmpty())
            }

            when (result) {
                is Result.Success -> _uiState.value = UiState.Success(result.data)
                is Result.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    private enum class GenerationType { SUMMARY, FLASHCARDS, MCQS }

}
