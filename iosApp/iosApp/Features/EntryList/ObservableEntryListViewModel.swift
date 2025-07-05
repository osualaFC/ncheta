//
//  ObservableEntryListViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import shared
import Combine

@MainActor
class ObservableEntryListViewModel: ObservableObject {
    
    private let sharedVm: EntryListViewModel
    
    @Published var entries: [NchetaEntry] = []
    
    private var observationTask: Task<Void, Never>?

    init() {
        // Use the Koin helper to get the shared ViewModel instance
        self.sharedVm = ViewModels().entryListViewModel
        
        // Initialize with the current value
        self.entries = self.sharedVm.entries.value as? [NchetaEntry] ?? []
        
        // Start a task to observe the flow for updates
        self.observationTask = Task {
            for await entriesList in self.sharedVm.entries {
                if let swiftEntries = entriesList as? [NchetaEntry] {
                    self.entries = swiftEntries
                }
            }
        }
    }
    
    deinit {
        observationTask?.cancel()
        sharedVm.clear()
        print("ObservableEntryListViewModel: deinit called")
    }
}
