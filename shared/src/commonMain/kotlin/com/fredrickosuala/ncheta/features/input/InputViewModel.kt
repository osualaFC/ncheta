package com.fredrickosuala.ncheta.features.input

import com.fredrickosuala.ncheta.data.model.Flashcard
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.data.model.InputSourceType
import com.fredrickosuala.ncheta.data.model.MultipleChoiceQuestion
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.services.Result
import com.fredrickosuala.ncheta.services.ContentGenerationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InputViewModel(
    private val coroutineScope: CoroutineScope,
    private val generationService: ContentGenerationService,
    private val repository: NchetaRepository
) {

    private val _uiState = MutableStateFlow<InputUiState>(InputUiState.Idle)
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()


    private var userApiKey: String? = "AIzaSyAGQujn9TJoQTf38EKeg7ZBUksoSgqKY_4" // Placeholder for User Story 2.1

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun onSummarizeClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateSummary(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateFlashcardsClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateFlashcards(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateQaClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateMcqs(_inputText.value, userApiKey!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun clearText() {
        _inputText.value = ""
    }

    fun resetUiState() {
        _uiState.value = InputUiState.Idle
    }

    fun updateUserApiKey(apiKey: String?) {
        this.userApiKey = apiKey
        println("InputViewModel: API Key updated to: $apiKey")
    }

    private fun validateInputs(): Boolean {
        if (userApiKey.isNullOrBlank()) {
            _uiState.value = InputUiState.Error("API Key is missing. Please add it in settings.")
            return false
        }
        if (inputText.value.isBlank()) {
            _uiState.value = InputUiState.Error("Input text cannot be empty.")
            return false
        }
        return true
    }

    @OptIn(ExperimentalUuidApi::class)
    fun saveGeneratedContent(title: String) {
        val currentState = _uiState.value

        if (currentState is InputUiState.Success) {
            val generatedData = currentState.data

            if (title.isBlank()) {
                _uiState.value = InputUiState.Error("Title cannot be empty.")
                return
            }

            val contentToSave: GeneratedContent = when (generatedData) {
                is String -> GeneratedContent.Summary(generatedData)
                is List<*> -> {
                    if (generatedData.all { it is Flashcard }) {
                        @Suppress("UNCHECKED_CAST")
                        GeneratedContent.FlashcardSet(generatedData as List<Flashcard>)
                    } else if (generatedData.all { it is MultipleChoiceQuestion }) {
                        @Suppress("UNCHECKED_CAST")
                        GeneratedContent.McqSet(generatedData as List<MultipleChoiceQuestion>)
                    } else {
                        _uiState.value = InputUiState.Error("Unsupported data type for saving.")
                        return
                    }
                }
                else -> {
                    _uiState.value = InputUiState.Error("Cannot save this type of content.")
                    return
                }
            }

            val newEntry = NchetaEntry(
                id = Uuid.random().toString(),
                title = title.trim(),
                sourceText = inputText.value,
                inputSourceType = InputSourceType.MANUAL,
                content = contentToSave
            )

            try {
                coroutineScope.launch {
                    repository.insertEntry(newEntry)
                }
                _uiState.value = InputUiState.Saved
            } catch (e: Exception) {
                _uiState.value = InputUiState.Error("Failed to save content: ${e.message}")
            }

            resetUiState()

        } else {
            _uiState.value = InputUiState.Error("No generated content is available to save.")
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}

sealed class InputUiState {
    data object Idle: InputUiState()
    data object Loading: InputUiState()
    data object Saved: InputUiState()
    data class Success(val data: Any): InputUiState()
    data class Error(val message: String): InputUiState()
}
