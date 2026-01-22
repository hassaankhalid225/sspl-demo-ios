import Foundation
import Combine

/// Singleton class to manage notification state between AppDelegate and SwiftUI views
class NotificationManager: ObservableObject {
    static let shared = NotificationManager()
    
    /// The join URL from notification
    @Published var joinUrl: String?
    
    /// The join code from notification
    @Published var joinCode: String?
    
    /// Flag to show/hide WebView
    @Published var showWebView: Bool = false
    
    private init() {}
    
    /// Called when a notification is tapped with session data
    func openSession(joinUrl: String, joinCode: String? = nil) {
        print("NotificationManager: Opening session with URL: \(joinUrl)")
        
        DispatchQueue.main.async {
            self.joinUrl = joinUrl
            self.joinCode = joinCode
            self.showWebView = true
        }
    }
    
    /// Called when WebView is dismissed
    func closeSession() {
        print("NotificationManager: Closing session")
        
        DispatchQueue.main.async {
            self.showWebView = false
            self.joinUrl = nil
            self.joinCode = nil
        }
    }
}
