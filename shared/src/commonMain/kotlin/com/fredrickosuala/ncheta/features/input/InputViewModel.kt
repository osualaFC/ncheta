package com.fredrickosuala.ncheta.features.input

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputViewModel(
    private val coroutineScope: CoroutineScope
) {
    constructor() : this(coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()))
    private val _inputText = MutableStateFlow("")
    
    val inputText: StateFlow<String> = _inputText.asStateFlow()


    private var userApiKey: String? = null // Placeholder for User Story 2.1

    fun onInputTextChanged(newText: String) {
        _inputText.value = newText
    }

    fun onSummarizeClicked() {
        // In future tasks (e.g., User Story 1.4), this will:
        // 1. Get the API key (from settings or paid tier logic).
        // 2. Show a loading state.
        // 3. Call a ContentGenerationService to generate the summary from inputText.value.
        // 4. Navigate to a results screen or update UI with the summary.
        // 5. Handle errors.
        if (inputText.value.isNotBlank()) {
            println("InputViewModel: Summarize requested for text: '${inputText.value}'. API Key: $userApiKey")
            // Example: Trigger generation
            // generateContent(ContentType.SUMMARY)
        } else {
            println("InputViewModel: Summarize requested but input text is empty.")
            // Optionally, provide feedback to the UI about empty input.
        }
    }

    fun onGenerateFlashcardsClicked() {
        // Similar to onSummarizeClicked, but for flashcards (User Story 2.2)
        if (inputText.value.isNotBlank()) {
            println("InputViewModel: Generate Flashcards requested for text: '${inputText.value}'. API Key: $userApiKey")
            // Example: Trigger generation
            // generateContent(ContentType.FLASHCARDS)
        } else {
            println("InputViewModel: Generate Flashcards requested but input text is empty.")
        }
    }

    fun onGenerateQaClicked() {
        // Similar to onSummarizeClicked, but for Q&A (User Story 2.4)
        if (inputText.value.isNotBlank()) {
            println("InputViewModel: Generate Q&A requested for text: '${inputText.value}'. API Key: $userApiKey")
            // Example: Trigger generation
            // generateContent(ContentType.QA)
        } else {
            println("InputViewModel: Generate Q&A requested but input text is empty.")
        }
    }

    // Placeholder for API key update logic (User Story 2.1)
    fun updateUserApiKey(apiKey: String?) {
        this.userApiKey = apiKey
        println("InputViewModel: API Key updated to: $apiKey")
    }


    // This is where the actual content generation call would be made.
    // This function would likely live in a more specialized ViewModel or a UseCase later.
    /*
    private fun generateContent(type: ContentType) {
        coroutineScope.launch {
            // _uiState.value = UiState.Loading
            // val result = contentGenerationService.generate(inputText.value, type, userApiKey)
            // when (result) {
            //    is Success -> _uiState.value = UiState.Success(result.data)
            //    is Error -> _uiState.value = UiState.Error(result.message)
            // }
        }
    }
    */

    fun clear() {
        coroutineScope.cancel()
    }
}
