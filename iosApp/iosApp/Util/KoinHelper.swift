//
//  KoinHelper.swift
//  iosApp
//
//  Created by fredrick osuala on 14/6/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared

struct KoinHelper {
    static func doInit() {

        KoinIOSKt.doInitKoin()
        print("Koin for iOS has been initialized.")
    }
}
