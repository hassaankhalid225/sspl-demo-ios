package com.sspl.di

import com.sspl.core.push.PushNotificationService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class IosPushHelper : KoinComponent {
    private val pushService: PushNotificationService by inject()

    suspend fun registerDeviceToken() {
        pushService.registerDeviceToken()
    }
}
