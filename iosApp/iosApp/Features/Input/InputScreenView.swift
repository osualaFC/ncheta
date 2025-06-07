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

    var body: some View {
        
        let isLoading = viewModel.uiState is UiStateLoading
        
        NavigationStack {
            VStack(spacing: 16) {
                
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
                        set: { newText in viewModel.onInputTextChanged(newText:newText)}
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
            .padding()
            .navigationTitle("NCHETA")
            .navigationBarTitleDisplayMode(.inline)
            .background(AppColors.subtleOffWhite.ignoresSafeArea())
            .disabled(isLoading)
            
            if isLoading {
                Color.black.opacity(0.4).ignoresSafeArea()
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(1.5)
            }
        }
        .onTapGesture { hideKeyboard() }
        .onChange(of: viewModel.uiState) { newState in
            if let successState = newState as? UiStateSuccess<AnyObject> {
                alertMessage = "Content generated successfully!"
                isShowingAlert = true
            } else if let errorState = newState as? UiStateError {
                alertMessage = errorState.message
                isShowingAlert = true
            }
        }
        .alert("NCHETA", isPresented: $isShowingAlert, presenting: alertMessage) {_ in 
            Button("ok") {
                viewModel.resetUiState()
            }
        } message: { message in
            Text(message)
        }
    }
    
    private func hideKeyboard() {
         UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
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
