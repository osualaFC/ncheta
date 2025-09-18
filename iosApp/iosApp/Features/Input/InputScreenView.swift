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
    
    @StateObject private var viewModel = ObservableInputViewModel()
    @EnvironmentObject private var appNavigationState: AppNavigationState
    var audioState: AudioRecorderState {
        return viewModel.audioState
    }
    
    // State variables to control UI based on ViewModel state
    @State private var showSaveDialog: Bool = false
    @State private var showConfirmationAlert: Bool = false
    @State private var alertMessage: String = ""
    @State private var newEntryTitle: String = ""
    @State private var showAuthSheet = false
    @State private var showSettings = false
    @State private var isShowingDocumentPicker = false
    @State private var showImagePicker = false
    @State private var imagePickerSourceType: ImagePicker.SourceType = .photoLibrary
    @State private var showImageSourceOptions = false
    
    var body: some View {
        
        let isLoggedIn = viewModel.isLoggedInState
        let isLoading = viewModel.uiState is InputUiState.Loading
        
        NavigationStack {
            
            ZStack {
                
                mainContentView
                    .disabled(isLoading || showSaveDialog)
                
                // --- Overlays ---
                if isLoading {
                    Color.black.opacity(0.4).ignoresSafeArea()
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .scaleEffect(1.5)
                }
                
                if showSaveDialog {
                    Color.black.opacity(0.4).ignoresSafeArea()
                    SaveEntryDialogView(
                        isPresented: $showSaveDialog,
                        title: $newEntryTitle,
                        onCancel: {
                            showSaveDialog = false
                            viewModel.resetUiState()
                            newEntryTitle = ""
                        },
                        onSave: {
                            if (isLoggedIn) {
                                viewModel.saveGeneratedContent(title: newEntryTitle)
                                showSaveDialog = false
                                viewModel.clearInputText()
                                appNavigationState.selectedTab = .entries
                            } else {
                                showAuthSheet = true
                            }
                            
                        }
                    )
                }
            }
            .navigationTitle("NCHETA")
            .navigationBarTitleDisplayMode(.inline)
            .background(AppColors.subtleOffWhite.ignoresSafeArea())
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
            .onReceive(viewModel.$uiState) { newState in
                
                if newState is InputUiState.Success {
                    showSaveDialog = true
                } else if newState is InputUiState.Saved {
                    alertMessage = "Saved successfully!"
                    showConfirmationAlert = true
                } else if let errorState = newState as? InputUiState.Error {
                    alertMessage = errorState.message
                    showConfirmationAlert = true
                }
            }
            .alert(alertMessage, isPresented: $showConfirmationAlert) {
                Button("OK") {
                    viewModel.resetUiState()
                }
            }
            .sheet(isPresented: $showAuthSheet) {
                AuthView(onAuthSuccess: {
                    showAuthSheet = false
                })
            }
        }
    }
    
    
    private var mainContentView: some View {
        VStack(spacing: 16) {
            HStack(spacing: 8) {
                //doc
                Button {
                    isShowingDocumentPicker = true
                } label: {
                    Label("", systemImage: "doc.badge.plus")
                        .frame(maxWidth: .infinity)
                        .foregroundColor(.black)
                    
                }
                .buttonStyle(.bordered)
                .tint(Color.white)
                .controlSize(.regular)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                )
                
                //image
                Button {
                    showImageSourceOptions = true
                } label: {
                    Label("", systemImage: "photo.badge.plus")
                        .frame(maxWidth: .infinity)
                        .foregroundColor(.black)
                    
                }
                .buttonStyle(.bordered)
                .tint(Color.white)
                .controlSize(.regular)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                )
                
                //audio
                Button {
                    let isRecording = audioState is AudioRecorderState.Recording
                    if isRecording {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                } label: {
                    let isRecording = audioState is AudioRecorderState.Recording
                    Label("", systemImage: isRecording ? "stop.fill" : "mic.fill")
                        .frame(maxWidth: .infinity)
                        .foregroundColor(audioState is AudioRecorderState.Recording ? .red : .black)
                }
                .buttonStyle(.bordered)
                .tint(Color.white)
                .controlSize(.regular)
                .overlay(
                    RoundedRectangle(cornerRadius: 10)
                        .stroke(Color.gray.opacity(0.4), lineWidth: 1)
                )
        
            
        }
        // Text Editor
        ZStack(alignment: .topLeading) {
            if viewModel.inputText.isEmpty {
                Text("Enter or extract text here")
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
        .confirmationDialog("Select Image Source", isPresented: $showImageSourceOptions) {
            Button("Camera") {
                self.imagePickerSourceType = .camera
                self.showImagePicker = true
            }
            Button("Photo Library") {
                self.imagePickerSourceType = .photoLibrary
                self.showImagePicker = true
            }
            Button("Cancel", role: .cancel) {}
        }
        
        Text("What would you like to do?")
            .font(AppFonts.interMedium(size: 18))
            .foregroundColor(AppColors.nearBlack)
        
        // Action Buttons
        VStack(spacing: 10) {
            ActionButton(text: "Summarize", action: viewModel.onSummarizeClicked)
            ActionButton(text: "Generate Flashcards", action: viewModel.onGenerateFlashcardsClicked)
            ActionButton(text: "Generate Q&A", action: viewModel.onGenerateQaClicked)
        }
    }
        .padding()
        .onTapGesture { hideKeyboard() }
        .sheet(isPresented: $isShowingDocumentPicker) {
            DocumentPicker { extractedText in
                viewModel.onInputTextChanged(newText: extractedText)
                isShowingDocumentPicker = false
            }
        }
        .sheet(isPresented: $showImagePicker) {
            ImagePicker(sourceType: imagePickerSourceType) { imageData in
                guard let data = imageData else { return }
                let byteArray = [UInt8](data)
                viewModel.getTextFromImage(imageData: byteArray)
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
                .padding(.vertical, 16)
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

