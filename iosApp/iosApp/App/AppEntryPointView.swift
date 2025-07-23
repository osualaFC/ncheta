//
//  AppEntryPointView.swift
//  iosApp
//
//  Created by fredrick osuala on 23/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct AppEntryPointView: View {
    
    @StateObject private var viewModel = MainViewModel()

    var body: some View {
       
        switch viewModel.hasCompletedOnboarding {
        case true:
            MainView()
        case false:
            OnboardingView(onOnboardingComplete: viewModel.setOnboardingComplete)
        case .some(_):
            ProgressView()
        case .none:
            ProgressView()
        }
    }
}
