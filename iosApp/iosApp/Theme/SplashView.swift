//
//  SplashView.swift
//  iosApp
//
//  Created by fredrick osuala on 10/9/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct SplashView: View {
    @Binding var isActive: Bool

    @State private var logoScale: CGFloat = 0.8
    @State private var logoOpacity: Double = 0.0

    var body: some View {
        ZStack {
           
            Color("PureWhite")
                .ignoresSafeArea()

            Image("splash_logo")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 160, height: 160)
                .scaleEffect(logoScale)
                .opacity(logoOpacity)
                .shadow(radius: 4)
        }
        .onAppear {
            // 1) entrance animation
            withAnimation(.spring(response: 0.6, dampingFraction: 0.7)) {
                logoScale = 1.0
                logoOpacity = 1.0
            }

            // 2) short hold then fade/scale out and set isActive to true
            DispatchQueue.main.asyncAfter(deadline: .now() + 1.05) {
                withAnimation(.easeOut(duration: 0.35)) {
                    logoScale = 0.9
                    logoOpacity = 0.0
                }
            }

            DispatchQueue.main.asyncAfter(deadline: .now() + 1.4) {
                isActive = true
            }
        }
    }
}

