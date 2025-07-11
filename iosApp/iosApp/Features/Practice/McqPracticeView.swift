//
//  McqPracticeView.swift
//  iosApp
//
//  Created by fredrick osuala on 11/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared


struct McqPracticeView: View {
    let practiceState: PracticeState
    let onSelectOption: (Int32) -> Void
    let onCheckAnswer: () -> Void
    let onNextQuestion: () -> Void
    let onRestart: () -> Void

    var body: some View {
        
        if practiceState.isPracticeComplete {
            
            VStack(spacing: 16) {
                Text("You've completed this quiz!")
                    .font(.title2)
                Button("Practice Again", action: onRestart)
                    .buttonStyle(.borderedProminent)
            }
        } else {
            
            let mcqSet = practiceState.entry.content as? GeneratedContentMcqSet
            let question = mcqSet?.items[Int(practiceState.currentQuestionIndex)]
            
            VStack(spacing: 20) {
                Text(question?.questionText ?? "Loading question...")
                    .font(.title2)
                    .fontWeight(.medium)
                    .multilineTextAlignment(.center)
                
                VStack(spacing: 12) {
                    ForEach(0..<(question?.options.count ?? 0), id: \.self) { index in
                        let isSelected = practiceState.selectedOptionIndex?.intValue == index
                        
                        OptionRowView(
                            text: question?.options[index] as? String ?? "",
                            isSelected: isSelected,
                            isCorrect: question?.correctOptionIndex.intValue == index,
                            isAnswerRevealed: practiceState.isAnswerRevealed
                        )
                        .onTapGesture {
                            onSelectOption(Int32(index))
                        }
                    }
                }
                
                Spacer()
                
                if practiceState.isAnswerRevealed {
                    Button(action: onNextQuestion) {
                        let isLastQuestion = practiceState.currentQuestionIndex == ((mcqSet?.items.count ?? 0) - 1)
                        Text(isLastQuestion ? "Finish" : "Next Question")
                            .frame(maxWidth: .infinity)
                    }
                    .buttonStyle(.borderedProminent)
                    .controlSize(.large)
                } else {
                    Button("Check Answer", action: onCheckAnswer)
                        .frame(maxWidth: .infinity)
                        .disabled(practiceState.selectedOptionIndex == nil)
                        .buttonStyle(.borderedProminent)
                        .controlSize(.large)
                }
            }
        }
    }
}


private struct OptionRowView: View {
    let text: String
    let isSelected: Bool
    let isCorrect: Bool
    let isAnswerRevealed: Bool
    
    var body: some View {
        HStack {
            Image(systemName: isSelected ? "largecircle.fill.circle" : "circle")
                .foregroundColor(isSelected ? .accentColor : .secondary)
            Text(text)
            Spacer()
        }
        .padding()
        .background(backgroundColor())
        .cornerRadius(8)
        .overlay(
            RoundedRectangle(cornerRadius: 8)
                .stroke(borderColor(), lineWidth: 1.5)
        )
    }
    

    private func backgroundColor() -> Color {
        guard isAnswerRevealed else { return Color(.systemGray6) }
        return isCorrect ? .green.opacity(0.15) : (isSelected ? .red.opacity(0.15) : Color(.systemGray6))
    }
    
  
    private func borderColor() -> Color {
        guard isSelected else { return .clear }
        guard isAnswerRevealed else { return .accentColor }
        return isCorrect ? .green : .red
    }
}

