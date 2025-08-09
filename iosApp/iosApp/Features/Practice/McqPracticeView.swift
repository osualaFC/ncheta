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
            completionView
        } else {
            quizView
        }
    }

   
    private var completionView: some View {
        VStack(spacing: 16) {
            Text("You've completed this quiz!")
                .font(.title2)
            Button("Practice Again", action: onRestart)
                .buttonStyle(.borderedProminent)
        }
    }

   
    private var quizView: some View {
       
        let mcqSet = practiceState.entry.content as? GeneratedContentMcqSet
        let question = mcqSet?.items[Int(practiceState.currentQuestionIndex)]
        
        return VStack(spacing: 20) {
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
                        isCorrect: question?.correctOptionIndex ?? 0 == index,
                        isAnswerRevealed: practiceState.isAnswerRevealed
                    )
                    .onTapGesture {
                        onSelectOption(Int32(index))
                    }
                }
            }
            
            Spacer()
            
            if practiceState.isAnswerRevealed {
                let isLastQuestion = practiceState.currentQuestionIndex == ((mcqSet?.items.count ?? 0) - 1)
                Button(isLastQuestion ? "Finish" : "Next Question", action: onNextQuestion)
                    .frame(maxWidth: .infinity)
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
        .padding()
    }
    

    private func backgroundColor() -> Color {
        if isAnswerRevealed && isCorrect {
            return Color.green.opacity(0.2)
        } else if isAnswerRevealed && isSelected && !isCorrect {
            return Color.red.opacity(0.2)
        }  else if isAnswerRevealed && !isSelected && isCorrect {
            return Color.red.opacity(0.2)
        } else {
            return Color(.systemGray6)
        }
    }
    
  
    private func borderColor() -> Color {
        if isAnswerRevealed && isCorrect {
            return Color.green.opacity(0.8)
        } else if isAnswerRevealed && isSelected && !isCorrect {
            return Color.red.opacity(0.8)
        } else {
            return Color(.separator)
        }
    }
}

