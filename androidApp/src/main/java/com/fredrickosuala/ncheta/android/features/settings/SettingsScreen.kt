package com.fredrickosuala.ncheta.android.features.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.fredrickosuala.ncheta.features.settings.AndroidSettingsViewModel
import com.fredrickosuala.ncheta.features.settings.SettingsUiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onKeySaved: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    viewModel: AndroidSettingsViewModel = koinViewModel()
) {
    val apiKey by viewModel.settingsViewModel.apiKey.collectAsState()
    val uiState by viewModel.settingsViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        append("You can get your free Gemini API key from Google AI Studio. ")
        pushStringAnnotation(tag = "URL", annotation = "https://aistudio.google.com/app/apikey")
        withStyle(style = SpanStyle(color = Color.Blue)) {
            append("Click here to get your key.")
        }
        pop()
    }

    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Success) {
            scope.launch {
                snackbarHostState.showSnackbar("API Key saved successfully!")
            }
           onKeySaved()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "API Key",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = apiKey,
                onValueChange = viewModel.settingsViewModel::onApiKeyChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Gemini API Key") },
                placeholder = { Text("Enter your key here") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )
            Button(
                onClick = viewModel.settingsViewModel::saveApiKey,
                enabled = uiState !is SettingsUiState.Saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState is SettingsUiState.Saving) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Save API Key")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ClickableText(
                text = annotatedString,
                style = MaterialTheme.typography.bodyMedium,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            uriHandler.openUri(annotation.item)
                        }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onNavigateToPaywall) {
                Text("Upgrade to Premium")
            }
        }
    }
}