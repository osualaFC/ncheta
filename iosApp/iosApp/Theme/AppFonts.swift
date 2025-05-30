//
//  AppFonts.swift
//  iosApp
//
//  Created by fredrick osuala on 30/5/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

struct AppFonts {

    static func interRegular(size: CGFloat) -> Font {
        return .custom("Inter-Regular", size: size)
    }

    static func interMedium(size: CGFloat) -> Font {
        return .custom("Inter-Medium", size: size)
    }

    static func interSemiBold(size: CGFloat) -> Font {
        return .custom("Inter-SemiBold", size: size)
    }

    static func interBold(size: CGFloat) -> Font {
        return .custom("Inter-Bold", size: size)
    }

    static var titleLarge: Font {
        interMedium(size: 22)
    }

    static var bodyLarge: Font {
        interRegular(size: 18)
    }

    static var bodyMedium: Font {
        interRegular(size: 16)
    }
  
}

struct TitleLargeStyle: ViewModifier {
    func body(content: Content) -> some View {
        content
            .font(AppFonts.titleLarge)
            .foregroundColor(AppColors.nearBlack)
    }
}

extension View {
    func titleLargeStyle() -> some View {
        self.modifier(TitleLargeStyle())
    }
}
