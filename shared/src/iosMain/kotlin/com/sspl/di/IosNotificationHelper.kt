package com.sspl.di

import com.sspl.core.models.SessionNotification
import com.sspl.core.repositories.NotificationRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Helper class for iOS to store notifications in NotificationRepository
 * Called from Swift AppDelegate when notifications are received
 */
class IosNotificationHelper : KoinComponent {
    private val notificationRepository: NotificationRepository by inject()
    
    /**
     * Store a session notification in the repository
     * Call this from AppDelegate when a push notification is received
     */
    fun storeNotification(
        sessionId: Int,
        joinCode: String,
        scenarioTitle: String?,
        joinUrl: String?,
        sessionType: String?
    ) {
        println("IosNotificationHelper: Storing session notification - sessionId=$sessionId, joinCode=$joinCode")
        
        val sessionNotification = SessionNotification(
            sessionId = sessionId,
            joinCode = joinCode,
            scenarioTitle = scenarioTitle,
            joinUrl = joinUrl
        )
        
        notificationRepository.addSessionNotification(sessionNotification)
        println("IosNotificationHelper: Session notification stored successfully")
    }

    /**
     * Store a generic notification in the repository
     */
    fun storeGenericNotification(title: String, message: String) {
        println("IosNotificationHelper: Storing generic notification - title=$title")
        notificationRepository.addNotification(title, message)
        println("IosNotificationHelper: Generic notification stored successfully")
    }
}
