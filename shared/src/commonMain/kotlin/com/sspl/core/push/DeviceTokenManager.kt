package com.sspl.core.push

expect class DeviceTokenManager() {
    suspend fun getToken(): String?
    fun getPlatform(): String
}
