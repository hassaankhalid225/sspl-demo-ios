package com.sspl.core.repositories

import com.sspl.core.apis.ProfileRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody

class DeviceTokenRepository(private val client: HttpClient) {
    
    suspend fun saveDeviceToken(
        deviceToken: String,
        platform: String
    ): Result<Boolean> {
        return try {
            client.put(ProfileRequest.UpdateDeviceToken) {
                setBody(mapOf(
                    "device_token" to deviceToken,
                    "device_platform" to platform
                ))
            }
            Result.success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
