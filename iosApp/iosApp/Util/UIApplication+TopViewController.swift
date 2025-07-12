//
//  UIApplication+TopViewController.swift
//  iosApp
//
//  Created by fredrick osuala on 12/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI

extension UIApplication {
    func topMostViewController() async -> UIViewController? {
        let keyWindow = await self.connectedScenes
            .filter { $0.activationState == .foregroundActive }
            .first(where: { $0 is UIWindowScene })
            .flatMap({ $0 as? UIWindowScene })?.windows
            .first(where: \.isKeyWindow)
        
        var topController = keyWindow?.rootViewController
        while let presentedViewController = topController?.presentedViewController {
            topController = presentedViewController
        }
        return topController
    }
}
