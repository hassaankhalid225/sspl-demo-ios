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
        
        // Store notification in repository
        storeNotificationInRepository(userInfo: userInfo)
        
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
        
        // Extract join_url from notification data
        if let joinUrl = userInfo["join_url"] as? String {
            print("FCM: Opening session with join_url: \(joinUrl)")
            let joinCode = userInfo["join_code"] as? String
            NotificationManager.shared.openSession(joinUrl: joinUrl, joinCode: joinCode)
        } else {
            print("FCM: No join_url found in notification")
        }
        
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
        
        // Store notification in repository
        storeNotificationInRepository(userInfo: userInfo)
        
        completionHandler(.newData)
    }
    
    // MARK: - Helper Methods
    
    /// Store notification in NotificationRepository via IosNotificationHelper
    private func storeNotificationInRepository(userInfo: [AnyHashable: Any]) {
        let helper = IosNotificationHelper()
        
        // Extract session data if available
        if let joinCode = userInfo["join_code"] as? String {
            let sessionIdString = userInfo["session_id"] as? String ?? "0"
            let sessionId = Int32(sessionIdString) ?? 0
            let scenarioTitle = userInfo["scenario_title"] as? String
            let joinUrl = userInfo["join_url"] as? String
            let sessionType = userInfo["type"] as? String
            
            print("FCM: Storing session notification in repository - sessionId=\(sessionId), joinCode=\(joinCode)")
            
            // Store in Kotlin repository via helper
            helper.storeNotification(
                sessionId: sessionId,
                joinCode: joinCode,
                scenarioTitle: scenarioTitle,
                joinUrl: joinUrl,
                sessionType: sessionType
            )
        } else {
            // Handle generic notification
            var title = "Notification"
            var body = ""
            
            // Extract from 'aps' payload
            if let aps = userInfo["aps"] as? [AnyHashable: Any],
               let alert = aps["alert"] as? [AnyHashable: Any] {
                title = alert["title"] as? String ?? "Notification"
                body = alert["body"] as? String ?? ""
            } else if let aps = userInfo["aps"] as? [AnyHashable: Any],
                      let alert = aps["alert"] as? String {
                body = alert
            }
            
            // If body is empty, try to get from direct keys (some FCM payloads)
            if body.isEmpty {
                title = userInfo["title"] as? String ?? userInfo["gcm.notification.title"] as? String ?? "Notification"
                body = userInfo["body"] as? String ?? userInfo["gcm.notification.body"] as? String ?? ""
            }
            
            if !body.isEmpty {
                print("FCM: Storing generic notification in repository - title=\(title)")
                helper.storeGenericNotification(title: title, message: body)
            } else {
                print("FCM: Skipping storage - no notification content found")
            }
        }
    }
}
