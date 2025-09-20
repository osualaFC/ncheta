//
//  ObservableAuthViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 12/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import shared
import Combine

@MainActor
class ObservableAuthViewModel: ObservableObject {
    
    private let sharedVm: AuthViewModel
    
    @Published var email = ""
    @Published var password = ""
    @Published var uiState: AuthUiState
    
    private var stateWatcher: Task<Void, Never>?
    
    init() {
        self.sharedVm = ViewModels().authViewModel
        self.uiState = self.sharedVm.uiState.value
        self.email = sharedVm.email.value as String
        self.password = sharedVm.password.value as String
        
        self.stateWatcher = Task {
            
            Task {
                for await text in sharedVm.email { self.email = text as String }
            }
            
            Task {
                for await text in sharedVm.password { self.password = text as String}
            }
            
            Task {
                for await state in sharedVm.uiState { self.uiState = state }
            }
        }
    }
    
    func signUp() { sharedVm.signUp() }
    func signIn() { sharedVm.signIn() }
    func resetState() { sharedVm.resetState() }
    func onEmailChanged(newEmail: String) {
        sharedVm.onEmailChanged(newEmail: newEmail)
    }
    func onPasswordChanged(newPassword: String) {
        sharedVm.onPasswordChanged(newPassword: newPassword)
    }
    func signInWithGoogleToken(idToken: String, accessToken: String) {
        sharedVm.signInWithGoogleToken(idToken: idToken, accessToken: accessToken)
    }
    func signInWithApple(idToken: String, nonce: String) {
        sharedVm.signInWithApple(idToken: idToken, nonce: nonce)
    }
    

    
    deinit {
        stateWatcher?.cancel()
        sharedVm.clear()
    }
}
