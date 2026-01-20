package com.sspl.core.push

import com.sspl.core.repositories.DeviceTokenRepository
import com.sspl.session.UserSession

class PushNotificationService(
    private val tokenManager: DeviceTokenManager,
    private val repository: DeviceTokenRepository,
    private val userSession: UserSession
) {
    suspend fun registerDeviceToken() {
        if (userSession.token.isNullOrEmpty()) return
        
        val deviceToken = tokenManager.getToken() ?: return
        val platform = tokenManager.getPlatform()
        
        repository.saveDeviceToken(deviceToken, platform)
    }
}
