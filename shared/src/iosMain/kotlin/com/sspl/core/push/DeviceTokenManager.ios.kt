package com.sspl.core.push

actual class DeviceTokenManager {
    actual suspend fun getToken(): String? {
        return TokenStorage.currentToken
    }

    actual fun getPlatform(): String = "ios"
}

object TokenStorage {
    var currentToken: String? = null
}
