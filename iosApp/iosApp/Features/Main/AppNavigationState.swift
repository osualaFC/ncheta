//
//  AppNavigationState.swift
//  iosApp
//
//  Created by fredrick osuala on 12/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import SwiftUI
import Combine

class AppNavigationState: ObservableObject {
    
    enum Tab {
        case create
        case entries
    }
    
    @Published var selectedTab: Tab = .create
}
