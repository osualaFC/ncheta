import SwiftUI
import FirebaseCore
import shared

@main
struct iOSApp: App {
    
    init() {
        
        FirebaseApp.configure()
        
        KoinHelper.doInit()
        
        RevenueCatModuleKt.initializeRevenueCat()
        
        Task {
            do {
                try await ViewModels().remoteConfigManager.fetchAndActivate()
                print("Remote Config fetched and activated successfully.")
            } catch {
                print("Error fetching Remote Config: \(error.localizedDescription)")
            }
        }
    }
    
    @State private var isActive = false

       var body: some Scene {
           WindowGroup {
               Group {
                   if isActive {
                       AppEntryPointView()
                   } else {
                       SplashView(isActive: $isActive)
                   }
               }
               .animation(.none, value: isActive) // avoid implicit animation for view swap
           }
       }
}
