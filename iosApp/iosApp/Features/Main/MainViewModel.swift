//
//  MainViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 23/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

// File: iosApp/iosApp/AppViewModel.swift (New File)
import Foundation
import shared
import Combine

@MainActor
class MainViewModel: ObservableObject {
    
    private let onboardingManager: OnboardingManager
    
    @Published var hasCompletedOnboarding: Bool? = nil
    @Published var isReadyToUseApp: Bool? = nil
    
    private var observationTask: Task<Void, Never>?
    private var readyWatcher: Task<Void, Never>?

    init() {
        self.onboardingManager = ViewModels().onboardingManager
        
        // Observe the onboarding status from the shared manager
        self.observationTask = Task {
            for await hasCompleted in self.onboardingManager.hasCompletedOnboarding {
                self.hasCompletedOnboarding = hasCompleted as? Bool ?? false
            }
        }
        
        self.readyWatcher = Task {
            for await isReady in self.onboardingManager.isReadyToUseApp {
                self.isReadyToUseApp = isReady as? Bool ?? false
            }
        }
    }
    
    func setOnboardingComplete() {
        Task {
            onboardingManager.setOnboardingCompleted()
        }
    }
    
    deinit {
        observationTask?.cancel()
        readyWatcher?.cancel()
    }
}
