//
//  ObservablePracticeViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 11/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import shared
import Combine

@MainActor
class ObservablePracticeViewModel: ObservableObject {
    
    private let sharedVm: PracticeViewModel

    @Published var uiState: PracticeUiState

    private var observationTask: Task<Void, Never>?

    init(entryId: String) {
        
        self.sharedVm = ViewModels().practiceViewModel
        
        self.uiState = self.sharedVm.uiState.value

        self.observationTask = Task {
            for await state in self.sharedVm.uiState {
                self.uiState = state
            }
        }
        
        self.sharedVm.loadEntry(entryId: entryId)
    }

    func flipCard() {
        sharedVm.flipCard()
    }

    func nextCard() {
        sharedVm.nextCard()
    }

    func restartPractice() {
        sharedVm.restartPractice()
    }
    
    func selectOption(optionIndex: Int32) {
        sharedVm.selectOption(optionIndex: optionIndex)
    }
    
    func checkAnswer() {
        sharedVm.checkAnswer()
    }
    
    func nextQuestion() {
        sharedVm.nextQuestion()
    }
    
    


    deinit {
        observationTask?.cancel()
        sharedVm.clear()
        print("ObservablePracticeViewModel: deinit called")
    }
}
