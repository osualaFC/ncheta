//
//  PracticeView.swift
//  iosApp
//
//  Created by fredrick osuala on 11/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import SwiftUI
import shared

struct PracticeView: View {
    
    let entryId: String
    
    @StateObject private var viewModel: ObservablePracticeViewModel
    
    @Environment(\.dismiss) private var dismiss
    
    init(entryId: String) {
        self.entryId = entryId
        self._viewModel = StateObject(wrappedValue: ObservablePracticeViewModel(entryId: entryId))
    }
    
    var body: some View {
        
        NavigationView {
            VStack {
                
                switch viewModel.uiState {
                case is PracticeUiState.Loading:
                    ProgressView()
                    
                case let successState as PracticeUiState.Success:
                    
                    switch successState.state.entry.content {
                    case let summary as GeneratedContentSummary:
                        SummaryView(summary: summary)
                        
                    case let flashcardSet as GeneratedContentFlashcardSet:
                        FlashcardPracticeView(
                            practiceState: successState.state,
                            onFlipCard: viewModel.flipCard,
                            onNextCard: viewModel.nextCard,
                            onRestart: viewModel.restartPractice
                        )
                        
                    case let mcqSet as GeneratedContentMcqSet:
                        McqPracticeView(
                            practiceState: successState.state,
                            onSelectOption: viewModel.selectOption,
                            onCheckAnswer: viewModel.checkAnswer,
                            onNextQuestion: viewModel.nextQuestion,
                            onRestart: viewModel.restartPractice
                        )
                        
                    default:
                        Text("Unknown content type.")
                    }
                    
                case let errorState as PracticeUiState.Error:
                    Text("Error: \(errorState.message)")
                        .foregroundColor(.red)
                        .multilineTextAlignment(.center)
                        .padding()
                    
                default:
                    EmptyView()
                }
            }
            .navigationTitle((viewModel.uiState as? PracticeUiState.Success)?.state.entry.title ?? "Practice")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
        }
    }
}


private struct SummaryView: View {
    let summary: GeneratedContentSummary
    
    var body: some View {
        ScrollView {
            Text(summary.text)
                .padding()
        }
    }
}
