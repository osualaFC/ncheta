package com.fredrickosuala.ncheta.android.features.practice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.navigation.AppHeader
import com.fredrickosuala.ncheta.data.model.GeneratedContent
import com.fredrickosuala.ncheta.features.practice.AndroidPracticeViewModel
import com.fredrickosuala.ncheta.features.practice.PracticeState
import com.fredrickosuala.ncheta.features.practice.PracticeUiState
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    entryId: String,
    onNavigateBack: () -> Unit,
    viewModel: AndroidPracticeViewModel = koinViewModel()
) {
    val uiState by viewModel.practiceViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = entryId) {
        viewModel.practiceViewModel.loadEntry(entryId)
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            AppHeader(
                title = (uiState as? PracticeUiState.Success)?.state?.entry?.title ?: "Practice",
                onBackArrowClick = onNavigateBack
            )

            Spacer(modifier = Modifier.weight(1f))

            when (val state = uiState) {
                is PracticeUiState.Loading -> CircularProgressIndicator()
                is PracticeUiState.Error -> Text(state.message, textAlign = TextAlign.Center)
                is PracticeUiState.Success -> {
                    when (val content = state.state.entry.content) {
                        is GeneratedContent.Summary -> {
                            Text(
                                text = content.text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.verticalScroll(rememberScrollState())
                                )
                        }
                        is GeneratedContent.FlashcardSet -> {
                            FlashcardPracticeView(
                                practiceState = state.state,
                                onFlipCard = { viewModel.practiceViewModel.flipCard() },
                                onNextCard = { viewModel.practiceViewModel.nextCard() },
                                onRestart = { viewModel.practiceViewModel.restartPractice() }
                            )
                        }
                        is GeneratedContent.McqSet -> {
                            McqPracticeView(
                                practiceState = state.state,
                                onSelectOption = viewModel.practiceViewModel::selectOption,
                                onCheckAnswer = viewModel.practiceViewModel::checkAnswer,
                                onNextQuestion = viewModel.practiceViewModel::nextQuestion,
                                onRestart = viewModel.practiceViewModel::restartPractice
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

        }
}
