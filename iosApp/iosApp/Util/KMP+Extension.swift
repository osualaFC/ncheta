//
//  KMP+Extension.swift
//  iosApp
//
//  Created by fredrick osuala on 5/7/2025.
//  Copyright © 2025 orgName. All rights reserved.
//


import Foundation
import shared

// By adding this extension, we are telling Swift that our KMP-generated
// base class is safe to send across concurrency domains.
// We use `@unchecked` because the compiler can't verify this for Kotlin code,
// but we know it's safe because our states are simple data holders.
extension InputUiState: @unchecked @retroactive Sendable {}

// We also need to mark each of the subclasses.
extension InputUiState.Idle: @unchecked Sendable {}
extension InputUiState.Loading: @unchecked Sendable {}
extension InputUiState.Saved: @unchecked Sendable {}
extension InputUiState.Success: @unchecked Sendable {}
extension InputUiState.Error: @unchecked Sendable {}


extension InputViewModel: @unchecked @retroactive Sendable {}
extension EntryListViewModel: @unchecked @retroactive Sendable {}
extension PracticeViewModel: @unchecked @retroactive Sendable {}
extension AuthViewModel: @unchecked @retroactive Sendable {}
extension SettingsViewModel: @unchecked @retroactive Sendable {}

extension NchetaEntry: @unchecked  @retroactive Sendable {}
extension PracticeUiState: @unchecked @retroactive Sendable {}
extension AuthUiState: @unchecked @retroactive Sendable {}
extension SettingsUiState: @unchecked @retroactive Sendable {}


extension NchetaEntry: @retroactive Identifiable {}
