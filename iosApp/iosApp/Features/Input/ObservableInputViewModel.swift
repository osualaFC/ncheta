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
    
    private var observationTask: Task<Void, Error>?

    init(sharedViewModel: InputViewModel = InputViewModel()) {
        self.sharedVm = sharedViewModel
        
        // Initialize with the current value from SkieSwiftStateFlow's .value property
        // SKIE exposes StateFlow.value directly. It might be NSString by default.
        if let initialKotlinValue = sharedVm.inputText.value as String? {
            self.inputText = initialKotlinValue
        } else if let initialKotlinNSStringValue = sharedVm.inputText.value as NSString? {
             self.inputText = initialKotlinNSStringValue as String
        }

        // Start a Swift Task to observe the inputText Flow (as AsyncSequence)
        self.observationTask = Task {
              for await nsStringValue in sharedVm.inputText {
                  if let swiftString = nsStringValue as String? {
                      self.inputText = swiftString
                  } else {
                      print("ObservableInputViewModel: Received non-string or nil from inputText async sequence")
                  }
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

    deinit {
        // Cancel the Swift Task when the ViewModel is deallocated
        observationTask?.cancel()
        sharedVm.clear() // Still need to clear your shared ViewModel's CoroutineScope
        print("ObservableInputViewModel: deinit called, observationTask cancelled, sharedViewModel cleared.")
    }
}
