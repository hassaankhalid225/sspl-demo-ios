package com.sspl.platform


import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sspl.core.getBaseClient
import com.sspl.session.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import org.jetbrains.skia.Image
import platform.CoreLocation.CLLocationManager
import platform.Foundation.*
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.localeWithLocaleIdentifier
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

actual suspend fun getClient(userSession: UserSession): HttpClient {
    return getBaseClient(userSession = userSession, engineFactory = Darwin) {
        engine {
            configureRequest {
                setAllowsCellularAccess(true)
            }
        }
    }
}

actual fun formatDateTime(dateTime: String, inFormat: String, outFormat: String): String {
    return try {
        val dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = inFormat  // Input format
        val utcZone = NSTimeZone.timeZoneWithAbbreviation("UTC")
        utcZone?.let { dateFormatter.timeZone = it }

        val date = dateFormatter.dateFromString(dateTime)
        dateFormatter.dateFormat = outFormat  // Output format
        dateFormatter.timeZone = NSTimeZone.defaultTimeZone()
        dateFormatter.locale = NSLocale.localeWithLocaleIdentifier("en_US")

        date?.let { dateFormatter.stringFromDate(it) } ?: dateTime
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


class IosGpsChecker : GpsChecker {
    override fun hasGps(): Boolean {
        return CLLocationManager.locationServicesEnabled()
    }

    override fun requestEnableLocationServices() {
        val settingsUrl = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
        settingsUrl?.let {
            if (UIApplication.sharedApplication.canOpenURL(it)) {
                UIApplication.sharedApplication.openURL(it)
            }
        }
    }
}

@Composable
actual fun createGpsChecker(): GpsChecker {
    return IosGpsChecker()
}


actual fun ByteArray.toBitmap() = Image.makeFromEncoded(this).toComposeImageBitmap()

actual fun platformConfiguration(): PlatformConfiguration = IosConfiguration()


