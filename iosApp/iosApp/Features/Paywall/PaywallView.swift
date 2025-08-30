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
                    
                    if let kmpOffering = successState.offerings as? [shared.ModelsOffering] {
                        PaywallContentView(
                            offerings: kmpOffering,
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
    
    private struct PaywallContentView: View {
        
        let offerings: [shared.ModelsOffering]
        let onPurchaseClicked: (shared.ModelsPackage) -> Void
        
        // State to keep track of the currently selected package
        @State private var selectedPackage: shared.ModelsPackage?
        
        var body: some View {
            VStack {
                Spacer()
                Text("Go Premium")
                    .font(.largeTitle).bold()
                
                VStack(alignment: .leading, spacing: 12) {
                    FeatureItemView(text: "Unlock cloud sync across all devices")
                    FeatureItemView(text: "Input text via audio")
                    FeatureItemView(text: "Advanced editing & merging")
                    FeatureItemView(text: "And lots more coming...")
                }
                .padding(.vertical, 32)
                
                    HStack(spacing: 16) {
                        ForEach(offerings, id: \.identifier) { offering in
                            if let pkg = offering.availablePackages.first {
                                PackageCardView(
                                    pkg: pkg,
                                    isSelected: selectedPackage?.identifier == pkg.identifier,
                                    onTap: { selectedPackage = pkg }
                                )
                            }
                        }
                    }
                
                Spacer()
                
                Button(action: {
                    if let selectedPackage = selectedPackage {
                        onPurchaseClicked(selectedPackage)
                    }
                }) {
                    Text("Upgrade Now")
                        .fontWeight(.semibold)
                        .frame(maxWidth: .infinity)
                }
                .buttonStyle(.borderedProminent)
                .controlSize(.large)
                .disabled(selectedPackage == nil)
                
                Text("Payments are managed by the App Store.")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .padding(.top, 8)
            }
            .padding(32)
            .onAppear {
                // Pre-select the first available package
                selectedPackage = offerings
                       .compactMap { $0.availablePackages.first }
                       .first
            }
        }
    }
}

private struct FeatureItemView: View {
    let text: String
    
    var body: some View {
        HStack {
            Image(systemName: "checkmark.circle.fill")
                .foregroundColor(.green)
            Text(text)
            Spacer()
        }
    }
}

private struct PackageCardView: View {
    let pkg: shared.ModelsPackage
    let isSelected: Bool
    let onTap: () -> Void
    
    var body: some View {
        
        cardContent
            .padding()
            .frame(maxWidth: .infinity, minHeight: 100)
            .background(isSelected ? Color.accentColor.opacity(0.1) : Color(.systemGray6))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.accentColor : Color.clear, lineWidth: 2)
            )
            .onTapGesture(perform: onTap)
    }
    
    private var cardContent: some View {
        VStack(spacing: 8) {
            Text(pkg.packageType.toDisplayString())
                .font(.headline)
                .fontWeight(.bold)
            
            Text(pkg.storeProduct.price.currencyCode + "" + pkg.storeProduct.price.formatted.replacingOccurrences(of: "$", with: ""))
                .font(.title).bold()
        }
    }
}

// Helper extension to make package types readable
extension ModelsPackageType {
    func toDisplayString() -> String {
        switch self {
        case .monthly: return "Monthly"
        case .annual: return "Annual"
        case .lifetime: return "Lifetime"
        default: return "Unknown"
        }
    }
}
