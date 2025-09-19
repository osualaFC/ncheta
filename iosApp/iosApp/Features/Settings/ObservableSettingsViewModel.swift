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
    @Published var user: NchetaUser?
    @Published var isPremium: Bool

    private var stateWatcher: Task<Void, Never>?
    private var apiKeyWatcher: Task<Void, Never>?
    private var userWatcher: Task<Void, Never>?
    private var isPremiumWatcher: Task<Void, Never>?

    init() {
        self.sharedVm = ViewModels().settingsViewModel
        self.uiState = self.sharedVm.uiState.value
        self.user = self.sharedVm.user.value
        self.isPremium = self.sharedVm.isPremium.value as? Bool ?? false

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
        
        self.userWatcher = Task {
            for await user in self.sharedVm.user {
                self.user = user
            }
        }
        
        self.isPremiumWatcher = Task {
            for await isPremium in self.sharedVm.isPremium {
                self.isPremium = isPremium as! Bool
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
    
    func signOut() {
        sharedVm.signOut()
    }
    
    deinit {
        stateWatcher?.cancel()
        apiKeyWatcher?.cancel()
        userWatcher?.cancel()
        isPremiumWatcher?.cancel()
        sharedVm.clear()
    }
}
