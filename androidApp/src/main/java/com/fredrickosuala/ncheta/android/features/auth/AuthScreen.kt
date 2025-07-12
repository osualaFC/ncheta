package com.fredrickosuala.ncheta.android.features.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.fredrickosuala.ncheta.features.auth.AndroidAuthViewModel
import com.fredrickosuala.ncheta.features.auth.AuthUiState
import com.google.android.gms.auth.api.identity.Identity
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
                                Toast.makeText(context, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
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
                        .padding(vertical = 16.dp),
                    enabled = uiState !is AuthUiState.Loading
                ) {
                    Text(if (isLoginMode) "Login" else "Sign Up")
                }

                TextButton(
                    onClick = { isLoginMode = !isLoginMode },
                    modifier = Modifier.padding(vertical = 8.dp),
                ) {
                    Text(if (isLoginMode) "Don't have an account? Sign Up" else "Already have an account? Login")
                }
            }

            if (uiState is AuthUiState.Loading) {
                CircularProgressIndicator()
            }
        }
    }
}