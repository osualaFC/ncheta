//
//  EntryListView.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct EntryListView: View {
    
    @StateObject private var viewModel = ObservableEntryListViewModel()
    @State private var entryToPractice: NchetaEntry?
    @State private var showDeleteAlert = false
    @State private var entryToDelete: NchetaEntry? = nil
    @State private var showSettings = false
    
    var body: some View {
        NavigationView {
            VStack {
                if viewModel.entries.isEmpty {
                    Text("You have no saved entries yet.")
                        .foregroundColor(.gray)
                } else {
                    List(viewModel.entries, id: \.id) { entry in
                        
                        EntryRowView(entry: entry)
                            .onTapGesture {
                                self.entryToPractice = entry
                            }
                            .swipeActions(edge: .trailing) {
                                Button(role: .destructive) {
                                    entryToDelete = entry
                                    showDeleteAlert = true
                                } label: {
                                    Label("Delete", systemImage: "trash.fill")
                                }
                            }
                    }
                }
            }
            .navigationTitle("My Entries")
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button {
                        showSettings = true
                    } label: {
                        Image(systemName: "gearshape.fill")
                    }
                }
            }
            .sheet(isPresented: $showSettings) {
                SettingsView()
            }
            .alert(
                "Delete Entry",
                isPresented: $showDeleteAlert,
                presenting: entryToDelete
            ) { entry in
                Button("Delete", role: .destructive) {
                    viewModel.deleteEntry(entryId: entry.id)
                }
                Button("Cancel", role: .cancel) {}
            } message: { entry in
                Text("Are you sure you want to permanently delete \(entry.title) from your entries ?")
            }
            .fullScreenCover(item: $entryToPractice) { entry in
                PracticeView(entryId: entry.id)
            }
        }
    }
}

struct EntryRowView: View {
    let entry: NchetaEntry
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(entry.title)
                .font(.headline)
            Text("Created: \(formatTimestamp(timestamp: entry.createdAt))")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .padding(.vertical, 8)
    }
    
    
    private func formatTimestamp(timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(timestamp / 1000))
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

struct EntryListView_Previews: PreviewProvider {
    static var previews: some View {
        EntryListView()
    }
}
