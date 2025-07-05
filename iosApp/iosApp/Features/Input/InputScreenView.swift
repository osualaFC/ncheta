//
//  InputScreenView.swift
//  iosApp
//
//  Created by fredrick osuala on 30/5/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct InputScreenView: View {

    @StateObject var viewModel = ObservableInputViewModel()
    @State private var placeholderText: String = "Enter or extract text here"

    @State private var alertMessage: String?
    @State private var isShowingAlert: Bool = false
    @State private var showSaveAlert: Bool = false
    @State private var showConfirmationAlert: Bool = false
    @State private var newEntryTitle: String = ""

    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                mainContentView
                    .disabled(viewModel.isLoading())

                if viewModel.isLoading() {
                    Color.black.opacity(0.4).ignoresSafeArea()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(1.5)
                }
            }
            .padding()
            .navigationTitle("NCHETA")
            .navigationBarTitleDisplayMode(.inline)
            .background(AppColors.subtleOffWhite.ignoresSafeArea())
            .onTapGesture { hideKeyboard() }
            .onReceive(viewModel.$uiState) { newState in
                if newState is InputUiState.Success {
                    alertMessage = "Content generated successfully!"
                    isShowingAlert = true
                } else if let errorState = newState as? InputUiState.Error {
                    alertMessage = errorState.message
                    isShowingAlert = true
                }
            }
            .alert("Content Generated Successfully", isPresented: $showSaveAlert) {
                TextField("Enter title", text: $newEntryTitle)
                Button("Save") {
                    viewModel.saveGeneratedContent(title: newEntryTitle)
                    newEntryTitle = ""
                }
                Button("Cancel", role: .cancel) {
                    viewModel.resetUiState()
                    newEntryTitle = ""
                }
            } message: {
                Text("Please provide a title to save this content for later practice.")
            }
            .alert("NCHETA", isPresented: $isShowingAlert, presenting: alertMessage) { _ in
                Button("ok") {
                    viewModel.resetUiState()
                }
            } message: { message in
                Text(message)
            }
        }
    }

    private var mainContentView: some View {
        VStack(alignment: .leading, spacing: 16) {
            ZStack(alignment: .topLeading) {
                if viewModel.inputText.isEmpty {
                    Text(placeholderText)
                        .font(AppFonts.bodyLarge)
                        .foregroundColor(AppColors.mediumGray)
                        .padding(.horizontal, 8)
                        .padding(.vertical, 12)
                }
                TextEditor(text: Binding(
                    get: { viewModel.inputText },
                    set: { newText in viewModel.onInputTextChanged(newText: newText) }
                ))
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
                    viewModel.onSummarizeClicked()
                }
                ActionButton(text: "Generate Flashcards") {
                    viewModel.onGenerateFlashcardsClicked()
                }
                ActionButton(text: "Generate Q&A") {
                    viewModel.onGenerateQaClicked()
                }
            }
        }
    }
}

@MainActor private func hideKeyboard() {
    UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder),
                                    to: nil, from: nil, for: nil)
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

