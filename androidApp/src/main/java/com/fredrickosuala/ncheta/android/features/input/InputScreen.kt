package com.fredrickosuala.ncheta.android.features.input

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
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.input.InputUiState
import com.fredrickosuala.ncheta.features.input.InputViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    viewModel: AndroidInputViewModel = koinViewModel()
) {

    val sharedVm = viewModel.inputViewModel

    val inputText by sharedVm.inputText.collectAsState()
    val uiState by sharedVm.uiState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is InputUiState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message,
                        withDismissAction = true
                        )
                }
                sharedVm.resetUiState()
            }
            is InputUiState.Saved -> {
                sharedVm.clearText()
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Content saved successfully"
                    )
                }
            }
            is InputUiState.Success -> {
                showSaveDialog = true
            }
            else -> {}
        }
    }

    if (showSaveDialog) {
        SaveContentDialog(
            onDismissRequest = {
                showSaveDialog = false
                sharedVm.resetUiState()
            },
            onSaveClicked = { title ->
                sharedVm.saveGeneratedContent(title)
                showSaveDialog = false
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                onValueChange = { sharedVm.onInputTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Enter or extract text here") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                enabled = uiState !is InputUiState.Loading,
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
                    onClick = { sharedVm.onSummarizeClicked() },
                    enabled = uiState !is InputUiState.Loading
                )
                ActionButton(
                    text = "Generate Flashcards",
                    onClick = { sharedVm.onGenerateFlashcardsClicked() },
                    enabled = uiState !is InputUiState.Loading
                )
                ActionButton(
                    text = "Generate Q&A",
                    onClick = { sharedVm.onGenerateQaClicked() },
                    enabled = uiState !is InputUiState.Loading
                )
            }
        }

        //--- Loading Overlay ---
        AnimatedVisibility(
            visible = uiState is InputUiState.Loading,
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