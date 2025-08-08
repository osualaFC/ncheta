//
//  OnboardingView.swift
//  iosApp
//
//  Created by fredrick osuala on 23/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//
// File: iosApp/iosApp/Features/Onboarding/OnboardingView.swift (New File)
import SwiftUI
import shared

struct OnboardingView: View {
    
    let onOnboardingComplete: () -> Void
    
    private let pages: [OnboardingPage] = [
        OnboardingPage(systemImageName: "pencil.and.outline", title: "Input Anything", description: "Type, paste, or upload text and documents to get started."),
        OnboardingPage(systemImageName: "wand.and.stars", title: "Generate Content", description: "Instantly create summaries, flashcards, or multiple-choice questions."),
        OnboardingPage(systemImageName: "bookmark.fill", title: "Save & Practice", description: "Save your generated content and practice to remember it forever.")
    ]
    
    @State private var currentPage = 0

    var body: some View {
        VStack {
        
            TabView(selection: $currentPage) {
                ForEach(pages.indices, id: \.self) { index in
                    OnboardingPageView(page: pages[index]).tag(index)
                }
            }
            .tabViewStyle(.page(indexDisplayMode: .never))
            
            // Custom Page Indicators
            HStack(spacing: 8) {
                ForEach(pages.indices, id: \.self) { index in
                    Circle()
                        .fill(index == currentPage ? Color.black : Color.gray.opacity(0.5))
                        .frame(width: 8, height: 8)
                }
            }
            .padding(.vertical)
            
            // "Next" / "Get Started" Button
            Button(action: {
                if currentPage < pages.count - 1 {
                    withAnimation {
                        currentPage += 1
                    }
                } else {
                    onOnboardingComplete()
                }
            }) {
                Text(currentPage < pages.count - 1 ? "Next" : "Get Started")
                    .fontWeight(.semibold)
                    .frame(maxWidth: .infinity)
            }
            .buttonStyle(.borderedProminent)
            .tint(.black)
            .controlSize(.large)
            .padding(32)
        }
    }
}


private struct OnboardingPageView: View {
    let page: OnboardingPage

    var body: some View {
        VStack(spacing: 24) {
            Image(systemName: page.systemImageName)
                .resizable()
                .scaledToFit()
                .frame(width: 120, height: 120)
                .foregroundColor(.black)
            
            Text(page.title)
                .font(.largeTitle)
                .fontWeight(.bold)
            
            Text(page.description)
                .font(.body)
                .multilineTextAlignment(.center)
        }
        .padding(.horizontal, 40)
    }
}

private struct OnboardingPage: Identifiable {
    let id = UUID()
    let systemImageName: String
    let title: String
    let description: String
}

struct OnboardingView_Previews: PreviewProvider {
    static var previews: some View {
        OnboardingView(
            onOnboardingComplete: {}
        )
    }
}
