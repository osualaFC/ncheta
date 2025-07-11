//
//  FlashcardPracticeView.swift
//  iosApp
//
//  Created by fredrick osuala on 11/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared

struct FlashcardPracticeView: View {
    let practiceState: PracticeState
    let onFlipCard: () -> Void
    let onNextCard: () -> Void
    let onRestart: () -> Void
    
    var body: some View {
        
        if let flashcardSet = practiceState.entry.content as? GeneratedContentFlashcardSet,
           let flashcards = flashcardSet.items as? [Flashcard],
           !flashcards.isEmpty {
            
            if practiceState.isPracticeComplete {
                VStack(spacing: 16) {
                    Text("You've completed this set!")
                        .font(.title2)
                    Button("Practice Again", action: onRestart)
                        .buttonStyle(.borderedProminent)
                }
            } else {
                VStack(spacing: 24) {
                    let card = flashcards[Int(practiceState.currentCardIndex)]
                    
                    FlashcardView(
                        frontText: card.front,
                        backText: card.back,
                        isFlipped: practiceState.isCardFlipped
                    )
                    .onTapGesture { onFlipCard() }
                    
                    Button(action: onNextCard) {
                        let isLastCard = practiceState.currentCardIndex == (flashcards.count - 1)
                        Text(isLastCard ? "Finish" : "Next Card")
                            .frame(maxWidth: 200)
                    }
                    .buttonStyle(.bordered)
                    .controlSize(.large)
                }
            }
        } else {
            
            Text("No flashcards available in this set.")
                .font(.headline)
                .foregroundColor(.secondary)
        }
    }
}

private struct FlashcardView: View {
    let frontText: String
    let backText: String
    let isFlipped: Bool
    
    var body: some View {
        ZStack {
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.gray, lineWidth: 1)
                .background(RoundedRectangle(cornerRadius: 12).fill(Color.white))
            
            ScrollView {
                Text(isFlipped ? backText : frontText)
                    .font(.title)
                    .multilineTextAlignment(.center)
                    .padding()
                    .scaleEffect(x: isFlipped ? -1 : 1, y: 1)
            }
        }
        .padding()
        .aspectRatio(1.5, contentMode: .fit)
        .rotation3DEffect(
            .degrees(isFlipped ? 180 : 0),
            axis: (x: 0.0, y: 1.0, z: 0.0)
        )
        .animation(.default, value: isFlipped)
    }
}
