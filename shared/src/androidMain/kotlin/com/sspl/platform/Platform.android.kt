package com.sspl.platform

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import com.sspl.core.getBaseClient
import com.sspl.session.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.compose.koinInject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

actual suspend fun getClient(userSession: UserSession): HttpClient {
    return getBaseClient(userSession = userSession, engineFactory = OkHttp) {
        engine {
            config {
                followRedirects(true)
            }
//            addInterceptor(interceptor)
//            addNetworkInterceptor(interceptor)
//            preconfigured = okHttpClientInstance
        }
    }
}

actual fun formatDateTime(dateTime: String, inFormat: String, outFormat: String): String {
    return try {
        println(">>>dateTime=$dateTime")
        val inputFormat = SimpleDateFormat(inFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val date = inputFormat.parse(dateTime)

        val outputFormat = SimpleDateFormat(outFormat, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        outputFormat.format(date!!)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

class AndroidGpsChecker(private val context: Context) : GpsChecker {
    override fun hasGps(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    override fun requestEnableLocationServices() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}

@Composable
actual fun createGpsChecker(): GpsChecker {
    val androidGpsChecker: AndroidGpsChecker = koinInject()
    return androidGpsChecker
}

actual fun ByteArray.toBitmap() = BitmapFactory.decodeByteArray(this, 0, size).asImageBitmap()

actual fun platformConfiguration(): PlatformConfiguration = AndroidConfiguration()




