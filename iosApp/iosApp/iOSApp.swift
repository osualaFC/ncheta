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
    
	var body: some Scene {
		WindowGroup {
            AppEntryPointView()
		}
	}
}
