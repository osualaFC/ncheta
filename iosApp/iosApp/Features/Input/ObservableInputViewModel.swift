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
    
    @Published var uiState: UiState<AnyObject>
    
    private var inputTextWatcherTask: Task<Void, Error>?
    private var uiStateWatcherTask: Task<Void, Never>?

    init() {
        self.sharedVm = ViewModels().inputViewModel
        self.uiState = sharedVm.uiState.value
        
        // Initialize with the current value from SkieSwiftStateFlow's .value property
        // SKIE exposes StateFlow.value directly. It might be NSString by default.
        if let initialKotlinValue = sharedVm.inputText.value as String? {
            self.inputText = initialKotlinValue
        } else if let initialKotlinNSStringValue = sharedVm.inputText.value as NSString? {
             self.inputText = initialKotlinNSStringValue as String
        }

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
        
        self.uiStateWatcherTask = Task {
            for await newState in sharedVm.uiState {
                self.uiState = newState
            }
        }
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
    
    func updateUserApiKey(apiKey: String) {
           sharedVm.updateUserApiKey(apiKey: apiKey)
    }
    
    func resetUiState() {
        sharedVm.resetUiState()
    }

    deinit {
        inputTextWatcherTask?.cancel()
        uiStateWatcherTask?.cancel()
        sharedVm.onCleared()
        print("ObservableInputViewModel: deinit called, observationTask cancelled, sharedViewModel cleared.")
    }
}
