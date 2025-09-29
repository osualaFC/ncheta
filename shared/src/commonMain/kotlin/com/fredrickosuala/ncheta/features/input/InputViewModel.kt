package com.fredrickosuala.ncheta.features.input

import com.fredrickosuala.ncheta.data.model.Flashcard
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.data.model.InputSourceType
import com.fredrickosuala.ncheta.data.model.MultipleChoiceQuestion
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.audio.AudioRecorder
import com.fredrickosuala.ncheta.domain.audio.AudioRecorderState
import com.fredrickosuala.ncheta.domain.config.RemoteConfigManager
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import com.fredrickosuala.ncheta.repository.AuthRepository
import com.fredrickosuala.ncheta.repository.NchetaRepository
import com.fredrickosuala.ncheta.repository.SettingsRepository
import com.fredrickosuala.ncheta.services.ContentGenerationService
import com.fredrickosuala.ncheta.services.Result
import com.revenuecat.purchases.kmp.Purchases
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val subscriptionManager: SubscriptionManager,
    private val remoteConfigManager: RemoteConfigManager
) {

    private val firestore = Firebase.firestore
    val isLoggedIn = authRepository.observeAuthState()
    val currentUser = authRepository.getCurrentUser()

    private val _uiState = MutableStateFlow<InputUiState>(InputUiState.Idle)
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    val audioRecorderState = audioRecorder.state

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = InputUiState.Error("Unexpected error: ${throwable.message}")
    }


    init {
        // 3. Listen for a successful recording from the recorder
        launchSafe {
            audioRecorder.state.collectLatest { state ->
                if (state is AudioRecorderState.Success) {
                    // When recording is successful, send the audio for transcription
                    transcribeAudio(state.audioData, state.mimeType)
                }
            }
        }

        launchSafe {
            repository.syncRemoteEntries(isPremium())
        }
    }

    private suspend fun isPremium(): Boolean {
        return subscriptionManager.getCustomerInfo().let {
            it.entitlements["premium"]?.isActive == true && currentUser?.uid == Purchases.sharedInstance.appUserID
        }
    }

    private var userApiKey = remoteConfigManager.getApiKey()

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    private suspend fun performLimitChecks(): Boolean {

        val user = authRepository.getCurrentUser()

        if (user == null) {
            _uiState.value = InputUiState.AuthRequired
            return false
        }

        // 1. Check Character Limit
        val charLimit = if (isPremium()) {
            remoteConfigManager.getPremiumCharLimit()
        } else {
            remoteConfigManager.getFreeCharLimit()
        }

        if (_inputText.value.length > charLimit) {
            _uiState.value =
                InputUiState.Error("Character limit exceeded. Premium users have a higher limit.")
            return false
        }

        // 2. Check Generation Limit (FREE users only)
        if (!isPremium()) {
            val dailyLimit = remoteConfigManager.getFreeMaxGenerationLimit()
            val usageDocRef = firestore.collection("usage_limits").document(user.uid)

            try {
                val usageDoc = usageDocRef.get()
                val currentCount = usageDoc.get<Long?>("count") ?: 0

                if (currentCount >= dailyLimit) {
                    _uiState.value = InputUiState.PremiumFeatureLocked
                    return false
                }

                // Update usage
                usageDocRef.set(mapOf( "count" to currentCount + 1))
            } catch (e: Exception) {
                _uiState.value = InputUiState.Error("Could not verify usage limit.")
                return false
            }
        }

        return true
    }

    fun onSummarizeClicked() {
        if (!validateInputs()) return

        launchSafe {

            val canProceed = performLimitChecks()
            if (!canProceed) return@launchSafe

            _uiState.value = InputUiState.Loading
            when (val result = generationService.generateSummary(_inputText.value, userApiKey)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateFlashcardsClicked() {
        if (!validateInputs()) return

        launchSafe {

            val canProceed = performLimitChecks()
            if (!canProceed) return@launchSafe

            _uiState.value = InputUiState.Loading
            when (val result = generationService.generateFlashcards(_inputText.value, userApiKey)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun onGenerateQaClicked() {
        if (!validateInputs()) return

        launchSafe {

            val canProceed = performLimitChecks()
            if (!canProceed) return@launchSafe

            _uiState.value = InputUiState.Loading
            when (val result = generationService.generateMcqs(_inputText.value, userApiKey)) {
                is Result.Success -> _uiState.value = InputUiState.Success(result.data)
                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    fun getTextFromImage(imageData: ByteArray) {
        if (!validateInputs(true)) return

        launchSafe {
            _uiState.value = InputUiState.Loading
            when (val result = generationService.getTextFromImage(imageData, userApiKey)) {
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
        if (userApiKey.isBlank()) {
            _uiState.value = InputUiState.Error("Something went wrong. Unable to fetch API key.")
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
                launchSafe {
                    repository.insertEntry(newEntry, isPremium())
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
        launchSafe {
            if (isPremium()) {
                audioRecorder.startRecording()
            } else {
                _uiState.value = InputUiState.PremiumFeatureLocked
            }
        }
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
    }


    private fun transcribeAudio(audioData: ByteArray, mimeType: String) {

        if (userApiKey.isBlank()) {
            _uiState.value = InputUiState.Error("Something went wrong. Unable to fetch API key.")
            return
        }

        _uiState.value = InputUiState.Loading
        launchSafe {
            when (val result = generationService.transcribeAudio(audioData, mimeType, userApiKey)) {
                is Result.Success -> {
                    _inputText.value = result.data
                    _uiState.value = InputUiState.Idle
                }

                is Result.Error -> _uiState.value = InputUiState.Error(result.message)
            }
        }
    }

    private fun launchSafe(block: suspend CoroutineScope.() -> Unit) {
        coroutineScope.launch(exceptionHandler) {
            try {
                block()
            } catch (e: Exception) {
                _uiState.value = InputUiState.Error("Unexpected error: ${e.message}")
            }
        }
    }


    fun clear() {
        coroutineScope.cancel()
        audioRecorder.onCleared()
    }
}

sealed class InputUiState {
    data object Idle : InputUiState()
    data object Loading : InputUiState()
    data object Saved : InputUiState()
    data object PremiumFeatureLocked : InputUiState()
    data object AuthRequired : InputUiState()
    data class Success(val data: Any) : InputUiState()
    data class Error(val message: String) : InputUiState()
}
