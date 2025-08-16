//
//  MainView.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct MainView: View {
    
    @StateObject private var appNavigationState = AppNavigationState()
    
    var body: some View {
        TabView(selection: $appNavigationState.selectedTab) {
            InputScreenView()
                .tabItem {
                    Label("Create", systemImage: "plus.circle.fill")
                }
                .tag(AppNavigationState.Tab.create)
            
            EntryListView()
                .tabItem {
                    Label("Entries", systemImage: "list.bullet")
                }
                .tag(AppNavigationState.Tab.entries)
        }
        .tint(.black)
        .environmentObject(appNavigationState)
    }
}
