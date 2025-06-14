import SwiftUI

@main
struct iOSApp: App {
    
    init() {
        KoinHelper.doInit()
    }
    
	var body: some Scene {
		WindowGroup {
			ContentView()
		}
	}
}
