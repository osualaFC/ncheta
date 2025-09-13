package com.fredrickosuala.ncheta.android.features.input

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import com.fredrickosuala.ncheta.android.theme.NchetaTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.fredrickosuala.ncheta.features.input.AndroidInputViewModel
import com.fredrickosuala.ncheta.features.input.InputUiState
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.launch
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.koin.androidx.compose.koinViewModel
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import com.fredrickosuala.ncheta.android.R
import com.fredrickosuala.ncheta.domain.audio.AudioRecorderState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    onSaved: () -> Unit = {},
    onNavigateToAuth: () -> Unit,
    onNavigateToPayWall: () -> Unit = {},
    viewModel: AndroidInputViewModel = koinViewModel()
) {

    val sharedVm = viewModel.inputViewModel

    val isLoggedIn by sharedVm.isLoggedIn.collectAsState(initial = false)

    val inputText by sharedVm.inputText.collectAsState()
    val uiState by sharedVm.uiState.collectAsState()
    val audioState by sharedVm.audioRecorderState.collectAsState()

    val isRecording = audioState is AudioRecorderState.Recording

    var showSaveDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val density = LocalDensity.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            if (isGranted) {
                sharedVm.startRecording()
            }
        }
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let {
                val imageData = context.contentResolver.openInputStream(it)?.readBytes()
                if (imageData != null) {
                    sharedVm.getTextFromImage(imageData)
                }
            }
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap: Bitmap? ->
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val imageData = stream.toByteArray()
                sharedVm.getTextFromImage(imageData)
            }
        }
    )

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            uri?.let {
                coroutineScope.launch {
                    try {
                        sharedVm.startLoading()
                        sharedVm.clearText()

                        val mimeType = context.contentResolver.getType(it)
                        var fileContent: String?

                        when (mimeType) {
                            "application/pdf" -> {
                                fileContent = context.contentResolver.openInputStream(it)?.use { inputStream ->
                                    val document = PDDocument.load(inputStream)
                                    val pdfStripper = PDFTextStripper()
                                    pdfStripper.getText(document).also { document.close() }
                                }
                            }
                            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword" -> {
                                fileContent = context.contentResolver.openInputStream(it)?.use { inputStream ->
                                    val doc = XWPFDocument(inputStream)
                                    val extractor = XWPFWordExtractor(doc)
                                    extractor.text.also { extractor.close() }
                                }
                            }
                            "text/plain" -> {
                                fileContent = context.contentResolver.openInputStream(it)?.use { inputStream ->
                                    BufferedReader(InputStreamReader(inputStream)).readText()
                                }
                            }
                            else -> {
                                // Fallback for other text types or unknown types
                                fileContent = context.contentResolver.openInputStream(it)?.use { inputStream ->
                                    BufferedReader(InputStreamReader(inputStream)).readText()
                                }
                            }
                        }

                        if (fileContent != null) {
                            sharedVm.onInputTextChanged(fileContent)
                        } else {
                            sharedVm.showError("Could not read content from the selected file.")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                        sharedVm.showError("Failed to read file.")
                    } finally {
                        sharedVm.resetUiState()
                    }
                }
            }
        }
    )


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
            is InputUiState.PremiumFeatureLocked -> {
                onNavigateToPayWall()
                sharedVm.resetUiState()
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

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source") },
            text = { Text("Would you like to take a photo or pick one from your gallery?") },
            confirmButton = {
                TextButton(onClick = {
                    cameraLauncher.launch(null)
                    showImageSourceDialog = false
                }) { Text("Take Photo") }
            },
            dismissButton = {
                TextButton(onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                    showImageSourceDialog = false
                }) { Text("Choose from Gallery") }
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
            //upload buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { documentPickerLauncher.launch(arrayOf("text/plain", "application/pdf", "application/vnd.openxmlformats-officedocument.wordprocessingml.document")) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload_doc),
                        contentDescription = "Upload Doc",
                        tint = Color.Black
                    )
                }
                OutlinedButton(
                    onClick = { showImageSourceDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_upload_image),
                        contentDescription = "Upload Image",
                        tint = Color.Black
                    )
                }
                OutlinedButton(
                    onClick = {
                        if (isRecording) {
                            sharedVm.stopRecording()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                              },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = if (isRecording)
                            painterResource(R.drawable.ic_stop)
                            else painterResource(id = R.drawable.ic_mic),
                        contentDescription = "Record Voice",
                        tint = if (isRecording) MaterialTheme.colorScheme.error else Color.Black
                    )
                }
            }
            // Main Text Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { sharedVm.onInputTextChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                label = { Text("Enter or extract text here") },
                textStyle = MaterialTheme.typography.bodyLarge,
                enabled = uiState !is InputUiState.Loading || audioState !is AudioRecorderState.Recording,
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

        // --- Voice Recording Indicator ---
        AnimatedVisibility(
            visible = audioState == AudioRecorderState.Recording,
            enter = slideInVertically { with(density) { -40.dp.roundToPx() } } + fadeIn(),
            exit = slideOutVertically { with(density) { -40.dp.roundToPx() } } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)

                .padding(bottom = 40.dp)
                .zIndex(1f)
        ) {
            VoiceRecordingIndicator(audioState = audioState, context = context)
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