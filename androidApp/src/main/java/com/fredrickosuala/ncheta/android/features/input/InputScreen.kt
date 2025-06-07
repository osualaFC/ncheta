package com.fredrickosuala.ncheta.android.features.input

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import com.fredrickosuala.ncheta.android.theme.NchetaTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fredrickosuala.ncheta.features.util.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    androidViewModel: AndroidInputViewModel = viewModel()
) {

    val sharedViewModel = androidViewModel.sharedViewModel
    val inputText by sharedViewModel.inputText.collectAsState()
    val uiState by sharedViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Error -> {
                Log.d("INPUT_SCREEN", "Error: ${state.message}")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        withDismissAction = true
                        )
                }
                sharedViewModel.resetUiState()
            }
            is UiState.Success -> {
                val successMessage = when (state.data) {
                    is String -> "Summary generated successfully"
                    is List<*> -> "Content generated successfully"
                    else -> "Success!"
                }
                Log.d("INPUT_SCREEN", "Success: ${state.data}")
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(successMessage)
                }
                sharedViewModel.resetUiState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "NCHETA",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { sharedViewModel.onInputTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Enter or extract text here") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                enabled = uiState !is UiState.Loading,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Text(
                "What would you like to do?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Summarize",
                    onClick = { sharedViewModel.onSummarizeClicked() },
                    enabled = uiState !is UiState.Loading
                )
                ActionButton(
                    text = "Generate Flashcards",
                    onClick = { sharedViewModel.onGenerateFlashcardsClicked() },
                    enabled = uiState !is UiState.Loading
                )
                ActionButton(
                    text = "Generate Q&A",
                    onClick = { sharedViewModel.onGenerateQaClicked() },
                    enabled = uiState !is UiState.Loading
                )
            }
        }

        //--- Loading Overlay ---
        AnimatedVisibility(
            visible = uiState is UiState.Loading,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = ButtonDefaults.outlinedButtonBorder(),
        enabled = enabled
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    NchetaTheme {
        InputScreen()
    }
}