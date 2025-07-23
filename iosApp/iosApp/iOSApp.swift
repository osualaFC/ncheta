import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {
    
    init() {
        
        FirebaseApp.configure()
        
        KoinHelper.doInit()
    }
    
	var body: some Scene {
		WindowGroup {
            AppEntryPointView()
		}
	}
}
