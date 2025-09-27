//
//  AppEntryPointView.swift
//  iosApp
//
//  Created by fredrick osuala on 28/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct AppEntryPointView: View {
    
    @StateObject private var viewModel = MainViewModel()
    @State private var showAuthSheet = false
    @State private var showPaywallSheet = false

    var body: some View {
        // Use a switch on our two state variables to determine the correct view
        switch (viewModel.hasCompletedOnboarding) {
        
        // States are still loading from disk
        case (nil):
            ProgressView()
            
        // Onboarding has not been completed yet
        case (false):
            OnboardingView(onOnboardingComplete: viewModel.setOnboardingComplete)
            
        // Onboarding is done, but the app isn't ready (API key is missing)
        // case (true, false):
        //     NavigationView {
        //           SettingsView(isFirstTimeSetup: true)
        //       }
            
        // Onboarding is done and the app is ready to use
        case (true):
            MainView()
            
        case (.some(_)):
            ProgressView()
        }
    }
}
