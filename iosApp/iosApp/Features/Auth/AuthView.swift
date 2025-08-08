//
//  AuthView.swift
//  iosApp
//
//  Created by fredrick osuala on 12/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import shared
import GoogleSignIn
import AuthenticationServices
import CryptoKit

struct AuthView: View {
    @StateObject private var viewModel = ObservableAuthViewModel()
    @State private var isLoginMode = true
    
    @State private var socialSignInError: String?
    @State private var isShowingSocialError = false
    @State private var currentNonce: String?
    
    let onAuthSuccess: () -> Void
    
    var body: some View {
        let isLoading = viewModel.uiState is AuthUiState.Loading
        
        VStack(spacing: 20) {
            Text(isLoginMode ? "Welcome Back" : "Create Account")
                .font(.largeTitle)
                .fontWeight(.bold)
            
            
            TextField("Email", text: $viewModel.email)
                .padding(.horizontal, 12)
                .keyboardType(.emailAddress)
                .textContentType(.emailAddress)
                .autocapitalization(.none)
                .textFieldStyle(.roundedBorder)
                .onChange(of: viewModel.email) { newValue in
                    viewModel.onEmailChanged(newEmail: newValue)
                }
            
            
            SecureField("Password", text: $viewModel.password)
                .padding(.horizontal, 12)
                .textContentType(isLoginMode ? .password : .newPassword)
                .textFieldStyle(.roundedBorder)
                .onChange(of: viewModel.password) { newValue in
                    viewModel.onPasswordChanged(newPassword: newValue)
                }
            
            SignInWithAppleButton(
                onRequest: { request in
                    let nonce = randomNonceString()
                    currentNonce = nonce
                    request.requestedScopes = [.fullName, .email]
                    request.nonce = sha256(nonce)
                },
                onCompletion: { result in
                    switch result {
                    case .success(let authorization):
                        guard let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential else { return }
                        guard let nonce = currentNonce else { return }
                        guard let idTokenData = appleIDCredential.identityToken else { return }
                        guard let idToken = String(data: idTokenData, encoding: .utf8) else { return }
                        
                        // Pass the credentials to the ViewModel
                        viewModel.signInWithApple(idToken: idToken, nonce: nonce)
                        
                    case .failure(let error):
                        print("Apple Sign-In Error: \(error.localizedDescription)")
                    }
                }
            )
            .signInWithAppleButtonStyle(.black)
            .frame(height: 50)
            .cornerRadius(8)
            
            Button(action: handleGoogleSignIn) {
                HStack {
                    Image("google_g_logo")
                        .resizable()
                        .renderingMode(.original)
                        .frame(width: 24, height: 24)
                    Text("Sign in with Google")
                        .font(.headline)
                        .foregroundColor(.black)
                }
                .frame(maxWidth: .infinity, maxHeight: 40)
            }
            .buttonStyle(.bordered)
            .tint(.white)
            .overlay(
                  RoundedRectangle(cornerRadius: 10)
                      .stroke(Color.gray.opacity(0.4), lineWidth: 1)
              )
            
            Button(action: {
                if isLoginMode { viewModel.signIn() } else { viewModel.signUp() }
            }) {
                HStack {
                    Spacer()
                    if isLoading {
                        ProgressView().tint(.white)
                    } else {
                        Text(isLoginMode ? "Login" : "Sign Up")
                    }
                    Spacer()
                }
            }
            .buttonStyle(.borderedProminent)
            .controlSize(.large)
            .tint(.black)
            .disabled(isLoading)
            
            Button(isLoginMode ? "Don't have an account? Sign Up" : "Already have an account? Login") {
                isLoginMode.toggle()
            }
            .font(.footnote)
        }
        .padding()
        .onChange(of: viewModel.uiState) { newState in
            if newState is AuthUiState.Success {
                onAuthSuccess()
            }
        }
        .alert("Sign-In Error", isPresented: $isShowingSocialError, presenting: socialSignInError) { _ in
            Button("OK") {}
        } message: { error in
            Text(error)
        }
        .alert("Error", isPresented: .constant(viewModel.uiState is AuthUiState.Error), actions: {
            Button("OK") { viewModel.resetState() }
        }, message: {
            if let errorState = viewModel.uiState as? AuthUiState.Error {
                Text(errorState.message)
            }
        })
    }
    
    private func handleGoogleSignIn() {
        // We still launch a Task to perform the async work
        Task {
            // We will call a new helper function that safely wraps the Google SDK call
            let idToken = await getGoogleIdToken()
            
            if let token = idToken {
                // If we got a token, pass it to the ViewModel
                viewModel.signInWithGoogleToken(idToken: token)
            } else {
                // If it's nil, it means sign-in failed or was cancelled
                socialSignInError = "Google Sign-In failed or was cancelled."
                isShowingSocialError = true
            }
        }
    }
}

private func getGoogleIdToken() async -> String? {
    // Get the top-most view controller
    guard let presentingViewController = await UIApplication.shared.topMostViewController() else {
        return nil
    }
    
    // Use withCheckedContinuation to bridge the callback-style API
    return await withCheckedContinuation { continuation in
        // Call the native Google Sign-In method
        GIDSignIn.sharedInstance.signIn(withPresenting: presentingViewController) { result, error in
            if let error = error {
                print("Google Sign-In Error: \(error.localizedDescription)")
                continuation.resume(returning: nil)
                return
            }
            
            guard let idToken = result?.user.idToken?.tokenString else {
                print("ID Token not found in Google Sign-In result")
                continuation.resume(returning: nil)
                return
            }
            
            // If we successfully get the token, resume the continuation with it
            continuation.resume(returning: idToken)
        }
    }
}

func randomNonceString(length: Int = 32) -> String {
    precondition(length > 0)
    let charset: [Character] =
        Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")
    var result = ""
    var remainingLength = length

    while remainingLength > 0 {
        let randoms: [UInt8] = (0 ..< 16).map { _ in
            var random: UInt8 = 0
            let errorCode = SecRandomCopyBytes(kSecRandomDefault, 1, &random)
            if errorCode != errSecSuccess {
                fatalError("Unable to generate random bytes. SecRandomCopyBytes failed with OSStatus \(errorCode)")
            }
            return random
        }
        randoms.forEach { random in
            if remainingLength == 0 { return }
            if random < charset.count {
                result.append(charset[Int(random)])
                remainingLength -= 1
            }
        }
    }
    return result
}

func sha256(_ input: String) -> String {
    let inputData = Data(input.utf8)
    let hashedData = SHA256.hash(data: inputData)
    let hashString = hashedData.compactMap {
        String(format: "%02x", $0)
    }.joined()
    return hashString
}
