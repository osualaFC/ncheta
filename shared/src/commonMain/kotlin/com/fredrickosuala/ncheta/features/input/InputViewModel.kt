package com.fredrickosuala.ncheta.features.input

import com.fredrickosuala.ncheta.data.model.Flashcard
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.data.model.InputSourceType
import com.fredrickosuala.ncheta.data.model.MultipleChoiceQuestion
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.audio.AudioRecorderState
import com.fredrickosuala.ncheta.domain.audio.AudioRecorder
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.services.Result
import com.fredrickosuala.ncheta.services.ContentGenerationService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class InputViewModel(
    private val coroutineScope: CoroutineScope,
    private val generationService: ContentGenerationService,
    private val repository: NchetaRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository,
    private val audioRecorder: AudioRecorder,
) {

    val isLoggedIn = authRepository.observeAuthState()

    private val _uiState = MutableStateFlow<InputUiState>(InputUiState.Idle)
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    val audioRecorderState = audioRecorder.state

    val handler = CoroutineExceptionHandler { _, throwable ->
        println("Caught coroutine exception: $throwable")
    }



    init {
        // 3. Listen for a successful recording from the recorder
        coroutineScope.launch {
            audioRecorder.state.collectLatest { state ->
                if (state is AudioRecorderState.Success) {
                    // When recording is successful, send the audio for transcription
                    transcribeAudio(state.audioData, state.mimeType)
                }
            }
        }
    }


    private var userApiKey: StateFlow<String?> = settingsRepository.getApiKey()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun onSummarizeClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateSummary(_inputText.value, userApiKey.value!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateFlashcardsClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateFlashcards(_inputText.value, userApiKey.value!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateQaClicked() {
        if (!validateInputs()) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when(val result = generationService.generateMcqs(_inputText.value, userApiKey.value!!)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun getTextFromImage(imageData: ByteArray) {
        if (!validateInputs(true)) return

        coroutineScope.launch {
            _uiState.value = InputUiState.Loading
            when (val result = generationService.getTextFromImage(imageData, userApiKey.value!!)) {
                is Result.Success -> {
                    _inputText.value = result.data
                    _uiState.value = InputUiState.Idle
                }
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun showError(errorMessage: String) {
        _uiState.value = InputUiState.Error(errorMessage)
    }

    fun startLoading() {
        _uiState.value = InputUiState.Loading
    }

    fun clearText() {
        _inputText.value = ""
    }

    fun resetUiState() {
        _uiState.value = InputUiState.Idle
    }

    private fun validateInputs(checkApiKeyOnly: Boolean = false): Boolean {
        if (userApiKey.value.isNullOrBlank()) {
            _uiState.value = InputUiState.Error("API Key is missing. Please add it in settings.")
            return false
        }
        if (checkApiKeyOnly) {
            return true
        } else if (inputText.value.isBlank()) {
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

    fun startRecording() {
        audioRecorder.startRecording()
    }

    fun stopRecording() {
        coroutineScope.launch(Dispatchers.Main) {
            audioRecorder.stopRecording()
        }
    }


    private fun transcribeAudio(audioData: ByteArray, mimeType: String) {
        val currentApiKey = userApiKey.value
        if (currentApiKey.isNullOrBlank()) {
            _uiState.value = InputUiState.Error("API Key is missing.")
            return
        }

        _uiState.value = InputUiState.Loading
        coroutineScope.launch {
            when (val result = generationService.transcribeAudio(audioData, mimeType, currentApiKey)) {
                is Result.Success -> {
                    _inputText.value = result.data
                    _uiState.value = InputUiState.Idle
                }
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun clear() {
        coroutineScope.cancel()
        audioRecorder.onCleared()
    }
}

sealed class InputUiState {
    data object Idle: InputUiState()
    data object Loading: InputUiState()
    data object Saved: InputUiState()
    data class Success(val data: Any): InputUiState()
    data class Error(val message: String): InputUiState()
}
