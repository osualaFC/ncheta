//
//  ObservablePaywallViewModel.swift
//  iosApp
//
//  Created by fredrick osuala on 29/8/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared
import Combine
import RevenueCat

@MainActor
class ObservablePaywallViewModel: ObservableObject {
    
    private let sharedVm: PaywallViewModel
    
    @Published var state: PaywallState
    @Published var promoCode: String = ""
    
    ///A publisher for one-time events
    let events = PassthroughSubject<shared.PaywallEvent, Never>()
    
    private var stateWatcher: Task<Void, Never>?
    private var eventWatcher: Task<Void, Never>?
    private var promoCodeWatcher: Task<Void, Never>?
    
    init() {
        self.sharedVm = ViewModels().paywallViewModel
        self.state = self.sharedVm.state.value
        self.promoCode = self.sharedVm.promoCode.value
        
        self.stateWatcher = Task {
            for await state in self.sharedVm.state {
                self.state = state
            }
        }
        
        self.eventWatcher = Task {
            for await event in self.sharedVm.events {
                self.events.send(event)
            }
        }
        
        self.promoCodeWatcher = Task {
            for await promoCode in self.sharedVm.promoCode {
                self.promoCode = promoCode
            }
        }
    }
    
    func onPurchaseClicked(pkg: ModelsPackage) {
        sharedVm.onPurchaseClicked(pkg: pkg)
    }
    
    func onPurchaseSuccess() {
        sharedVm.onPurchaseSuccess()
    }
    
    func onPromoCodeChanged(_ newCode: String) {
            sharedVm.onPromoCodeChanged(newCode: newCode)
        }

        func applyPromoCode() {
            sharedVm.applyPromoCode()
        }
    
    deinit {
        stateWatcher?.cancel()
        eventWatcher?.cancel()
        promoCodeWatcher?.cancel()
        sharedVm.clear()
    }
}
