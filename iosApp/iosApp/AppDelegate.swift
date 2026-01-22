import UIKit
import UserNotifications
import Firebase
import shared

class AppDelegate: NSObject, UIApplicationDelegate, MessagingDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {

        // Configure Firebase
        FirebaseApp.configure()
        
        // Set delegates for Firebase Messaging and UNUserNotificationCenter
        Messaging.messaging().delegate = self
        UNUserNotificationCenter.current().delegate = self

        // Request notification permissions
        UNUserNotificationCenter.current()
            .requestAuthorization(options: [.alert, .badge, .sound]) { granted, error in
                print("FCM: Notification permission granted: \(granted)")
                if let error = error {
                    print("FCM: Permission request error: \(error)")
                }
                if granted {
                    DispatchQueue.main.async {
                        application.registerForRemoteNotifications()
                    }
                }
            }

        return true
    }

    // MARK: - APNs Token Registration
    
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        print("FCM: APNs device token received")
        Messaging.messaging().apnsToken = deviceToken
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("FCM: Push registration failed: \(error)")
    }

    // MARK: - MessagingDelegate
    
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        print("FCM: Registration token received: \(fcmToken ?? "nil")")
        
        guard let token = fcmToken else { return }
        
        let helper = IosPushHelper()
        Task {
            try? await helper.registerDeviceToken(token: token)
        }
    }
    
    // MARK: - UNUserNotificationCenterDelegate
    
    /// Handle notifications when app is in foreground
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let userInfo = notification.request.content.userInfo
        print("FCM: ⚡️ Notification received in foreground!")
        print("FCM: userInfo = \(userInfo)")
        
        // Let Messaging know about the message for Analytics
        Messaging.messaging().appDidReceiveMessage(userInfo)
        
        // Show notification banner even when app is in foreground
        completionHandler([.banner, .sound, .badge])
    }
    
    /// Handle notification tap
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let userInfo = response.notification.request.content.userInfo
        print("FCM: Notification tapped: \(userInfo)")
        
        // TODO: Handle notification tap - navigate to specific screen if needed
        
        completionHandler()
    }
    
    // MARK: - Background/Silent Notification Handler
    
    func application(
        _ application: UIApplication,
        didReceiveRemoteNotification userInfo: [AnyHashable: Any],
        fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void
    ) {
        print("FCM: 📩 Background/Silent notification received!")
        print("FCM: userInfo = \(userInfo)")
        
        // Let Messaging know about the message for Analytics
        Messaging.messaging().appDidReceiveMessage(userInfo)
        
        completionHandler(.newData)
    }
}
