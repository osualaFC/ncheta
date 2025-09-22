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
                Color(.systemGroupedBackground).ignoresSafeArea()
                
                switch viewModel.state {
                case is PaywallState.Loading:
                    ProgressView()
                    
                case let successState as PaywallState.Success:
                    if let kmpOfferings = successState.offerings as? [shared.ModelsOffering],
                       let firstOffering = kmpOfferings.first {
                        PaywallContentView(
                            offering: firstOffering,
                            viewModel: viewModel
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
                // Fetch the native offerings when the view appears
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
                    if let kmpPackage = requestPurchase.pkg as? shared.ModelsPackage {
                        purchase(kmpPackage: kmpPackage)
                    }
                }
            }
        }
    }
    
    private func purchase(kmpPackage: shared.ModelsPackage) {
        guard let nativePackage = self.nativeOffering?.package(identifier: kmpPackage.identifier) else {
            print("Could not find matching native package to purchase.")
            return
        }
        Task {
            do {
                let result = try await Purchases.shared.purchase(package: nativePackage)
                if !result.userCancelled && result.customerInfo.entitlements.all["premium"]?.isActive == true {
                    viewModel.onPurchaseSuccess()
                    onPurchaseSuccess()
                }
            } catch {
                print("Purchase failed: \(error.localizedDescription)")
            }
        }
    }
}


private struct PaywallContentView: View {
    let offering: shared.ModelsOffering
    @ObservedObject var viewModel: ObservablePaywallViewModel
    
    @State private var selectedPackage: shared.ModelsPackage?
    @State private var showPromoField = false
    
    var body: some View {
        ScrollView {
            VStack {
                Text("Go Premium")
                    .font(.largeTitle).bold()
                    .padding(.top, 32)
                
                VStack(alignment: .leading, spacing: 12) {
                    FeatureItemView(text: "Unlock cloud sync across all devices")
                    FeatureItemView(text: "Input text via audio")
                    FeatureItemView(text: "And lots more...")
                }
                .padding(.vertical, 32)
                
                if let packages = offering.availablePackages as? [shared.ModelsPackage] {
                    HStack(spacing: 16) {
                        ForEach(packages, id: \.identifier) { pkg in
                            PackageCardView(
                                pkg: pkg,
                                isSelected: selectedPackage?.identifier == pkg.identifier,
                                onTap: { selectedPackage = pkg }
                            )
                        }
                    }
                }
                
                // Promo Code Section
                VStack {
                    Toggle("I have a promo code", isOn: $showPromoField.animation())
                    
                    if showPromoField {
                        HStack {
                            TextField("Enter code", text: $viewModel.promoCode)
                                .textFieldStyle(.roundedBorder)
                                .onChange(of: viewModel.promoCode) { viewModel.onPromoCodeChanged($0) }
                            
                            Button("Apply") { viewModel.applyPromoCode() }
                                .buttonStyle(.bordered)
                                .disabled(viewModel.promoCode.isEmpty)
                        }
                    }
                }
                .padding(.vertical)
                
            }
            .padding(.horizontal, 16)
        }
        .safeAreaInset(edge: .bottom) {
            VStack(spacing: 8) {
                Button(action: {
                    if let selectedPackage = selectedPackage {
                        viewModel.onPurchaseClicked(pkg: selectedPackage)
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
            }
            .padding(16)
            .background(.thinMaterial)
        }
        .onAppear {
            if let packages = offering.availablePackages as? [shared.ModelsPackage] {
                selectedPackage = packages.first
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
