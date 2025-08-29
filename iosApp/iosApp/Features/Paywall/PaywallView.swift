//
//  PaywallView.swift
//  iosApp
//
//  Created by fredrick osuala on 29/8/2025.
//  Copyright © 2025 orgName. All rights reserved.
//

import SwiftUI
import shared
import RevenueCat

struct PaywallView: View {
    
    @StateObject private var viewModel = ObservablePaywallViewModel()
    
    @State private var nativeOffering: RevenueCat.Offering?
    
    let onPurchaseSuccess: () -> Void
    let onDismiss: () -> Void
    
    var body: some View {
        NavigationView {
            ZStack {
                
                switch viewModel.state {
                case is PaywallState.Loading:
                    ProgressView()
                    
                case let successState as PaywallState.Success:
                    
                    if let kmpOffering = successState.offering as? shared.ModelsOffering {
                        PaywallContentView(
                            offering: kmpOffering,
                            onPurchaseClicked: { kmpPackage in
                                viewModel.onPurchaseClicked(pkg: kmpPackage)
                            }
                        )
                    } else {
                        Text("Could not display products.")
                    }
                    
                case let errorState as PaywallState.Error:
                    Text(errorState.message)
                        .padding()
                        .multilineTextAlignment(.center)
                    
                default:
                    EmptyView()
                }
            }
            .navigationTitle("Upgrade to Premium")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close", action: onDismiss)
                }
            }
            .onAppear {
                // When the view appears, fetch the native offerings from the RevenueCat SDK
                Task {
                    do {
                        self.nativeOffering = try await Purchases.shared.offerings().current
                    } catch {
                        print("Error fetching native offerings: \(error.localizedDescription)")
                    }
                }
            }
            .onReceive(viewModel.events) { event in
                // Listen for the purchase request event from the KMP ViewModel
                if case let requestPurchase as shared.PaywallEvent.RequestPurchase = event {
                    // Call our helper function with the KMP package
                    purchase(kmpPackage: requestPurchase.pkg)
                }
            }
        }
    }
    
    // This helper function bridges the KMP package to the native package and makes the purchase
    private func purchase(kmpPackage: shared.ModelsPackage) {
        // Find the native package that matches the KMP package's identifier
        guard let nativePackage = self.nativeOffering?.package(identifier: kmpPackage.identifier) else {
            print("Could not find matching native package to purchase.")
            return
        }
        Task {
            do {
                let result = try await Purchases.shared.purchase(package: nativePackage)
                // Check if the user cancelled and if the entitlement is now active
                if !result.userCancelled && result.customerInfo.entitlements.all["premium"]?.isActive == true {
                    // Tell the ViewModel the purchase was a success
                    viewModel.onPurchaseSuccess()
                    // Tell the UI to dismiss the paywall
                    onPurchaseSuccess()
                }
            } catch {
                print("Purchase failed: \(error.localizedDescription)")
                // You could show an error alert here
            }
        }
    }
    
    // This sub-view displays the content of the paywall
    private struct PaywallContentView: View {
        let offering: shared.ModelsOffering
        let onPurchaseClicked: (shared.ModelsPackage) -> Void
        
        var body: some View {
            VStack(spacing: 20) {
                Text("Go Premium")
                    .font(.largeTitle).bold()
                
                Text("Unlock cloud sync to access your entries on all your devices.")
                    .font(.headline)
                    .multilineTextAlignment(.center)
                if let packages = offering.availablePackages as? [shared.ModelsPackage] {
                    ForEach(packages, id: \.identifier) { pkg in
                        Button {
                            onPurchaseClicked(pkg)
                        } label: {
                            Text("\(pkg.storeProduct.title) - \(pkg.storeProduct.price.formatted)")
                                .fontWeight(.semibold)
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .controlSize(.large)
                    }
                }
                Text("Payments are managed by the App Store.")
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding(32)
        }
    }
}
