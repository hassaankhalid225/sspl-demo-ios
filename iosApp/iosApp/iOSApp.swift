import SwiftUI

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate
    
    @StateObject private var notificationManager = NotificationManager.shared

    var body: some Scene {
        WindowGroup {
            ContentView()
                .fullScreenCover(isPresented: $notificationManager.showWebView) {
                    if let joinUrl = notificationManager.joinUrl {
                        SessionWebViewScreen(joinUrl: joinUrl)
                            .onDisappear {
                                notificationManager.closeSession()
                            }
                    }
                }
        }
    }
}
