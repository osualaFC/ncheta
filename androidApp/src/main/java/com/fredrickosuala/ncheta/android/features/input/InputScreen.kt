package com.fredrickosuala.ncheta.android.features.input

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import com.fredrickosuala.ncheta.android.theme.NchetaTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.android.navigation.AppHeader
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.input.InputUiState
import com.fredrickosuala.ncheta.features.input.InputViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onSaved: () -> Unit = {},
    onNavigateToAuth: () -> Unit,
    viewModel: AndroidInputViewModel = koinViewModel()
) {


    val sharedVm = viewModel.inputViewModel

    val isLoggedIn by sharedVm.isLoggedIn.collectAsState(initial = false)

    val inputText by sharedVm.inputText.collectAsState()
    val uiState by sharedVm.uiState.collectAsState()
    var showSaveDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is InputUiState.Error -> {
                coroutineScope.launch {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                sharedVm.resetUiState()
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
                if (isLoggedIn) {
                    sharedVm.saveGeneratedContent(title)
                    sharedVm.clearText()
                    Toast.makeText(context, "Content saved successfully", Toast.LENGTH_SHORT).show()
                    onSaved()
                } else {
                    onNavigateToAuth()
                }
                showSaveDialog = false
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            AppHeader("NCHETA", showBackArrow = false) { }
            // Main Text Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { sharedVm.onInputTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Enter or extract text here") },
                textStyle = MaterialTheme.typography.bodyLarge,
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

            // Action Buttons
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

        // --- Loading Overlay ---
        AnimatedVisibility(
            visible = uiState is InputUiState.Loading,
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
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
        enabled = enabled,
        contentPadding = PaddingValues(vertical = 16.dp)
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
        InputScreen({}, {})
    }
}