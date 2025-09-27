import SwiftUI
import FirebaseCore
import shared

@main
struct iOSApp: App {
    
    init() {
        
        FirebaseApp.configure()
        
        KoinHelper.doInit()
        
        RevenueCatModuleKt.initializeRevenueCat()
        
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
