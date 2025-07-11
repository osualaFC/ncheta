package com.fredrickosuala.ncheta.features.practice

import com.fredrickosuala.ncheta.data.model.Flashcard
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.repository.NchetaRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val coroutineScope: CoroutineScope,
    private val repository: NchetaRepository
) {

    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadEntry(entryId: String) {
        _uiState.value = PracticeUiState.Loading
        coroutineScope.launch {
            val entry = repository.getEntryById(entryId)
            if (entry != null) {
                _uiState.value = PracticeUiState.Success(PracticeState(entry = entry))
            } else {
                _uiState.value = PracticeUiState.Error("Entry not found.")
            }
        }
    }

    fun flipCard() {
        (_uiState.value as? PracticeUiState.Success)?.let { successState ->
            _uiState.value = successState.copy(
                state = successState.state.copy(isCardFlipped = !successState.state.isCardFlipped)
            )
        }
    }

    fun nextCard() {
        (_uiState.value as? PracticeUiState.Success)?.let { successState ->
            val currentState = successState.state
            val cardCount =
                (currentState.entry.content as? GeneratedContent.FlashcardSet)?.items?.size ?: 0

            if (currentState.currentCardIndex + 1 >= cardCount) {
                _uiState.value = successState.copy(
                    state = currentState.copy(isPracticeComplete = true)
                )
            } else {
                val nextIndex = currentState.currentCardIndex + 1
                _uiState.value = successState.copy(
                    state = currentState.copy(currentCardIndex = nextIndex, isCardFlipped = false)
                )
            }
        }
    }

        fun restartPractice() {
            (_uiState.value as? PracticeUiState.Success)?.let { successState ->
                _uiState.value = successState.copy(
                    state = successState.state.copy(
                        currentCardIndex = 0,
                        isCardFlipped = false,
                        isPracticeComplete = false
                    )
                )
            }
        }


    fun selectOption(optionIndex: Int) {
        (_uiState.value as? PracticeUiState.Success)?.let { successState ->
            if (!successState.state.isAnswerRevealed) {
                _uiState.value = successState.copy(
                    state = successState.state.copy(selectedOptionIndex = optionIndex)
                )
            }
        }
    }

    fun checkAnswer() {
        (_uiState.value as? PracticeUiState.Success)?.let { successState ->
            if (successState.state.selectedOptionIndex != null) {
                _uiState.value = successState.copy(
                    state = successState.state.copy(isAnswerRevealed = true)
                )
            }
        }
    }

    fun nextQuestion() {
        (_uiState.value as? PracticeUiState.Success)?.let { successState ->
            val currentState = successState.state
            val questionCount = (currentState.entry.content as? GeneratedContent.McqSet)?.items?.size ?: 0
            if (questionCount > 0) {

                if (currentState.currentQuestionIndex + 1 >= questionCount) {
                    _uiState.value = successState.copy(
                        state = currentState.copy(isPracticeComplete = true)
                    )
                } else {
                    val nextIndex = currentState.currentQuestionIndex + 1
                    _uiState.value = successState.copy(
                        state = currentState.copy(
                            currentQuestionIndex = nextIndex,
                            selectedOptionIndex = null,
                            isAnswerRevealed = false
                        )
                    )
                }
            }
        }
    }

    fun clear() {
        coroutineScope.cancel()
    }
}

data class PracticeState(
    val entry: NchetaEntry,
    val currentCardIndex: Int = 0,
    val isCardFlipped: Boolean = false,
    val isPracticeComplete: Boolean = false,
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false
)


sealed class PracticeUiState {
    data object Loading : PracticeUiState()
    data class Error(val message: String) : PracticeUiState()
    data class Success(val state: PracticeState) : PracticeUiState()
}