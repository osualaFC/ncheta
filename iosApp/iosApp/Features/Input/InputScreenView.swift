//
//  InputScreenView.swift
//  iosApp
//
//  Created by fredrick osuala on 30/5/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct InputScreenView: View {
    // @StateObject var viewModel = InputViewModel()
    @State private var inputText: String = ""
    @State private var placeholderText: String = "Enter or extract text here"

    var body: some View {
        
        NavigationStack {
            VStack(spacing: 16) {
                
                ZStack(alignment: .topLeading) {
                    if inputText.isEmpty {
                        Text(placeholderText)
                            .font(AppFonts.bodyLarge)
                            .foregroundColor(AppColors.mediumGray)
                            .padding(.horizontal, 8)
                            .padding(.vertical, 12)
                    }
                    TextEditor(text: $inputText)
                        .font(AppFonts.bodyLarge)
                        .foregroundColor(AppColors.nearBlack)
                        .frame(maxWidth: .infinity, minHeight: 150, maxHeight: .infinity)
                        .scrollContentBackground(.hidden)
                        .background(Color.clear)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(AppColors.lightGray, lineWidth: 1)
                        )
                }
                .layoutPriority(1)

                Text("What would you like to do?")
                    .font(AppFonts.interMedium(size: 18))
                    .foregroundColor(AppColors.nearBlack)

                VStack(spacing: 10) {
                    ActionButton(text: "Summarize") {
                        // TODO: Call ViewModel to process summary with inputText
                        print("Summarize clicked with text: \(inputText)")
                    }
                    ActionButton(text: "Generate Flashcards") {
                        // TODO: Call ViewModel to process flashcards with inputText
                        print("Flashcards clicked with text: \(inputText)")
                    }
                    ActionButton(text: "Generate Q&A") {
                        // TODO: Call ViewModel to process Q&A with inputText
                        print("Q&A clicked with text: \(inputText)")
                    }
                }
            }
            .padding()
            .navigationTitle("NCHETA")
            .navigationBarTitleDisplayMode(.inline)
            .background(AppColors.subtleOffWhite.ignoresSafeArea())
        }
    }
}

struct ActionButton: View {
    let text: String
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(text)
                .font(AppFonts.interMedium(size: 16))
                .foregroundColor(AppColors.nearBlack)
                .padding(.vertical, 10)
                .frame(maxWidth: .infinity)
                .overlay(
                    RoundedRectangle(cornerRadius: 8)
                        .stroke(AppColors.lightGray, lineWidth: 1)
                )
        }
        .background(AppColors.pureWhite)
        .cornerRadius(8)
    }
}

struct InputScreenView_Previews: PreviewProvider {
    static var previews: some View {
        InputScreenView()
    }
}
