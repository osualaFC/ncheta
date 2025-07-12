//
//  ObservableInputViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 30/5/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import Combine
import shared

@MainActor
class ObservableInputViewModel: ObservableObject {
    
    private let sharedVm: InputViewModel
    
    @Published var inputText: String = ""
    
    @Published var uiState: InputUiState
    
    @Published var isLoggedInState: Bool = false
    private var authStateWatcherTask: Task<Void, Never>?
    
    private var inputTextWatcherTask: Task<Void, Error>?
    private var uiStateWatcherTask: Task<Void, Never>?
    
    init() {
        
        self.sharedVm = ViewModels().inputViewModel
        self.uiState = sharedVm.uiState.value
        
        
        // Start a Swift Task to observe the inputText Flow (as AsyncSequence)
        self.inputTextWatcherTask = Task {
            for await nsStringValue in sharedVm.inputText {
                if let swiftString = nsStringValue as String? {
                    self.inputText = swiftString
                } else {
                    print("ObservableInputViewModel: Received non-string or nil from inputText async sequence")
                }
            }
        }
        
        // Start a Swift Task to observe the uiState Flow (as AsyncSequence)
        self.uiStateWatcherTask = Task {
            for await newState in sharedVm.uiState {
                self.uiState = newState
            }
        }
        
        self.isLoggedInState = (self.sharedVm.isLoggedIn.value as? Bool) ?? false
        
        self.authStateWatcherTask = Task {
            for await isLoggedIn in self.sharedVm.isLoggedIn {
                self.isLoggedInState = isLoggedIn as? Bool ?? false
            }
        }
        
    }
    
    func isLoading() -> Bool {
        return self.uiState is InputUiState.Loading
    }
    
    func onInputTextChanged(newText: String) {
        sharedVm.onInputTextChanged(newText: newText)
    }
    
    func onSummarizeClicked() {
        sharedVm.onSummarizeClicked()
    }
    
    func onGenerateFlashcardsClicked() {
        sharedVm.onGenerateFlashcardsClicked()
    }
    
    func onGenerateQaClicked() {
        sharedVm.onGenerateQaClicked()
    }
    
    func saveGeneratedContent(title: String) {
        sharedVm.saveGeneratedContent(title: title)
    }
    
    func updateUserApiKey(apiKey: String) {
        sharedVm.updateUserApiKey(apiKey: apiKey)
    }
    
    func resetUiState() {
        sharedVm.resetUiState()
    }
    
    func clearInputText() {
        sharedVm.clearText()
    }
    
    deinit {
        inputTextWatcherTask?.cancel()
        uiStateWatcherTask?.cancel()
        authStateWatcherTask?.cancel()
        sharedVm.clear()
        print("ObservableInputViewModel: deinit called, observationTask cancelled, sharedViewModel cleared.")
    }
}
