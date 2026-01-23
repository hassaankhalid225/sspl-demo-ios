package com.sspl.core.push

import com.sspl.core.repositories.DeviceTokenRepository
import com.sspl.session.UserSession

class PushNotificationService(
    private val tokenManager: DeviceTokenManager,
    private val repository: DeviceTokenRepository,
    private val userSession: UserSession
) {
    suspend fun registerDeviceToken() {
        if (userSession.token.isNullOrEmpty()) {
            println("PushNotificationService: User not logged in, skipping token registration.")
            return
        }
        
        val deviceToken = tokenManager.getToken() ?: run {
            println("PushNotificationService: Device token not found.")
            return
        }
        val platform = tokenManager.getPlatform()
        
        println("PushNotificationService: Registering device token: $deviceToken for platform: $platform")
        
        repository.saveDeviceToken(deviceToken, platform).onSuccess {
            println("PushNotificationService: Device token registered successfully.")
        }.onFailure {
            println("PushNotificationService: Failed to register device token: ${it.message}")
        }
    }
}
