package com.sspl.di

import com.sspl.core.push.PushNotificationService
import com.sspl.core.push.TokenStorage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.native.ObjCName 

class IosPushHelper : KoinComponent {
    private val pushService: PushNotificationService by inject()

    @Throws(Exception::class) 
    suspend fun registerDeviceToken(token: String) {
        println("IosPushHelper: Received device token: $token")
        TokenStorage.currentToken = token
        
        pushService.registerDeviceToken()
    }
}