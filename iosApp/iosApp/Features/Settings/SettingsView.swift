//
//  SettingsView.swift
//  iosApp
//
//  Created by fredrick osuala on 28/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

import SwiftUI
import shared

struct SettingsView: View {
    @StateObject private var viewModel = ObservableSettingsViewModel()
    @Environment(\.dismiss) private var dismiss
    
    @State private var showAuthSheet = false
    @State private var showPaywallSheet = false
    
    var isFirstTimeSetup: Bool = false
    
    var body: some View {
        let isSaving = viewModel.uiState is SettingsUiState.Saving
        let user = viewModel.user
        let isPremium = viewModel.isPremium
        
        NavigationView {
            Form {
                // --- Account & Premium Sections ---
                if !isFirstTimeSetup {
                    // --- Account Section ---
                    Section(header: Text("Account")) {
                        if user == nil {
                            // 2. CHANGED: Button action now just toggles the local state
                            Button("Sign In") {
                                showAuthSheet = true
                            }
                        } else {
                            HStack {
                                Text(user?.email ?? "Logged In")
                                Spacer()
                                if isPremium {
                                    Image(systemName: "checkmark.circle.fill")
                                        .foregroundColor(.green)
                                }
                            }
                            Button("Sign Out", role: .destructive) { viewModel.signOut() }
                        }
                    }
                    
                    // --- Premium Section ---
                        Section(header: Text("Premium")) {
                            if !isPremium {
                            if user == nil {
                                // If user is not logged in, show an instructional message
                                Text("Please sign in or create an account to upgrade.")
                                    .foregroundColor(.secondary)
                            } else if !isPremium {
                                // If user is logged in AND not premium, show the button
                                Button("Upgrade to Premium") {
                                    showPaywallSheet = true
                                }
                            }
                        } else {
                                Button("Restore Subscription") { viewModel.restoreSubscription() }
                            }

                    }
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                if !isFirstTimeSetup {
                    ToolbarItem(placement: .cancellationAction) {
                        Button("Done") { dismiss() }
                    }
                }
            }
            .onChange(of: viewModel.uiState) { newState in
                if newState is SettingsUiState.Success {
                    if !isFirstTimeSetup { dismiss() }
                }
            }
            .sheet(isPresented: $showAuthSheet) {
                AuthView(onAuthSuccess: { showAuthSheet = false })
            }
            .sheet(isPresented: $showPaywallSheet) {
                PaywallView(
                    onPurchaseSuccess: { showPaywallSheet = false },
                    onDismiss: { showPaywallSheet = false }
                )
            }
        }
    }
}
