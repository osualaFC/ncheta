//
//  ObservableSettingsViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 28/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared
import Combine

@MainActor
class ObservableSettingsViewModel: ObservableObject {
    
    private let sharedVm: SettingsViewModel

    @Published var apiKey: String = ""
    @Published var uiState: SettingsUiState

    private var stateWatcher: Task<Void, Never>?
    private var apiKeyWatcher: Task<Void, Never>?

    init() {
        self.sharedVm = ViewModels().settingsViewModel
        self.uiState = self.sharedVm.uiState.value

        self.apiKey = self.sharedVm.apiKey.value as? String ?? ""
        self.apiKeyWatcher = Task {
            for await key in self.sharedVm.apiKey {
                self.apiKey = key as? String ?? ""
            }
        }
        
        self.stateWatcher = Task {
            for await state in self.sharedVm.uiState {
                self.uiState = state
            }
        }
    }
    
    func onApiKeyChanged(_ newKey: String) {
        sharedVm.onApiKeyChanged(newKey: newKey)
    }
    
    func saveApiKey() {
        sharedVm.saveApiKey()
    }
    
    func resetUiState() {
        sharedVm.resetUiState()
    }
    
    deinit {
        stateWatcher?.cancel()
        apiKeyWatcher?.cancel()
        sharedVm.clear()
    }
}
