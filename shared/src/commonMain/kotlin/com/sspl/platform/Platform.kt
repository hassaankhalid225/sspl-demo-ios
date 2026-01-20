package com.sspl.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import com.sspl.session.UserSession
import com.sspl.storage.Storage
import io.ktor.client.HttpClient

interface Platform {
    val name: String
}

expect suspend fun getClient(userSession: UserSession): HttpClient

expect fun formatDateTime(dateTime: String, inFormat: String, outFormat: String): String

expect fun platformConfiguration():PlatformConfiguration
expect fun ByteArray.toBitmap(): ImageBitmap

interface GpsChecker {
    fun hasGps(): Boolean
    fun requestEnableLocationServices()
}

@Composable
expect fun createGpsChecker(): GpsChecker

