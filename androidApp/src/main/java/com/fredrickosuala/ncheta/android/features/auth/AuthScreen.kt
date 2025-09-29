package com.fredrickosuala.ncheta.android.features.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.fredrickosuala.ncheta.features.auth.AndroidAuthViewModel
import com.fredrickosuala.ncheta.features.auth.AuthUiState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AndroidAuthViewModel = koinViewModel()
) {

    val sharedVm = viewModel.authViewModel
    val email by sharedVm.email.collectAsState()
    val password by sharedVm.password.collectAsState()
    val uiState by sharedVm.uiState.collectAsState()

    var isLoginMode by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                credentialManager = CredentialManager.create(context)
            )
        )
    }

    val annotatedText = buildAnnotatedString {
        if (isLoginMode) {
            append("Don't have an account? ")

            withStyle(
                style = SpanStyle(
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Sign Up")
            }
        } else {
            append("Already have an account? ")

            withStyle(
                style = SpanStyle(
                    color = Color(0xFF6200EE),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Login")
            }
        }
    }


    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> {
                onAuthSuccess()
            }

            is AuthUiState.Error -> {
                coroutineScope.launch {
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                sharedVm.resetState()
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (isLoginMode) "Welcome Back" else "Create Account",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = sharedVm::onEmailChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = sharedVm::onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                GoogleSignInButton(
                    onClick = {
                        coroutineScope.launch {
                            val idToken = googleAuthUiClient.signIn()
                            if (idToken != null) {
                                sharedVm.signInWithGoogleToken(idToken)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Google Sign-In failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                Button(
                    onClick = {
                        if (isLoginMode) sharedVm.signIn() else sharedVm.signUp()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(50.dp),
                    enabled = uiState !is AuthUiState.Loading
                ) {
                    Text(if (isLoginMode) "Login" else "Sign Up")
                }

                TextButton(
                    onClick = { isLoginMode = !isLoginMode },
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Text(annotatedText)
                }
            }

            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator()
            }
        }
    }
}