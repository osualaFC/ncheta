//
//  SettingsView.swift
//  iosApp
//
//  Created by fredrick osuala on 28/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct SettingsView: View {
    
    @StateObject private var viewModel = ObservableSettingsViewModel()
    @Environment(\.dismiss) private var dismiss
    var isPresentedModally: Bool = true
    
    var body: some View {
        let isSaving = viewModel.uiState is SettingsUiState.Saving
        
        NavigationView {
            Form {
                Section(header: Text("API Key")) {
                    SecureField("Enter your Gemini API Key", text: $viewModel.apiKey)
                        .onChange(of: viewModel.apiKey) { newValue in
                            viewModel.onApiKeyChanged(newValue)
                        }
                }
                
                Section(footer: Text("Ncheta uses the Gemini API for content generation. You must provide your own free developer key.")) {
                    Link("Click here to get your Gemini API key from Google AI Studio", destination: URL(string: "https://aistudio.google.com/app/apikey")!)
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if isPresentedModally {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Cancel") {
                            dismiss()
                        }
                    }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button("Save") {
                        viewModel.saveApiKey()
                    }
                    .disabled(isSaving)
                }
            }
            .onChange(of: viewModel.uiState) { newState in
                if newState is SettingsUiState.Success {
                    dismiss()
                }
            }
        }
    }
}
