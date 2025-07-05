//
//  MainView.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct MainView: View {
    
    var body: some View {
        TabView {
            InputScreenView()
                .tabItem {
                    Label("Create", systemImage: "plus.circle.fill")
                }
            
            EntryListView()
                .tabItem {
                    Label("Review", systemImage: "list.bullet")
                }
        }
    }
}
