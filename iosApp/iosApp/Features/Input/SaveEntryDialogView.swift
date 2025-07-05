//
//  SaveEntryDialogView.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI

struct SaveEntryDialogView: View {
    // Binding to control the visibility of this dialog
    @Binding var isPresented: Bool
    
    // Binding to the text field's content
    @Binding var title: String
    
    // Closures for the button actions
    let onCancel: () -> Void
    let onSave: () -> Void
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Save Entry")
                .font(.headline)
            
            Text("Please provide a title to save this content for later practice.")
                .font(.subheadline)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)

            TextField("Enter title", text: $title)
                .textFieldStyle(.roundedBorder)
            
            HStack(spacing: 12) {
                Button(action: {
                    onCancel()
                }) {
                    Text("Cancel")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.bordered)
                .controlSize(.large)
                
                Button(action: {
                    onSave()
                }) {
                    Text("Save")
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .disabled(title.isEmpty) // Disable save button if title is empty
            }
        }
        .padding(24)
        .background(Material.regular) // Gives a nice blurred background effect
        .cornerRadius(20)
        .shadow(radius: 10)
        .padding(32) // Padding around the dialog to the screen edges
    }
}
