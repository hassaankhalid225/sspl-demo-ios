package com.sspl.platform.shared

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.darwin.NSObject
import platform.UIKit.UIApplicationOpenURLOptionsKey
import kotlinx.cinterop.*

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 30/01/2025.
 * se.muhammadimran@gmail.com
 */

@OptIn(ExperimentalForeignApi::class)
actual class UrlOpener {
    @OptIn(ExperimentalForeignApi::class)
    actual fun openUrl(url: String) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(nsUrl, emptyMap<Any?, Any>()) { success ->
                println("URL Opened: $success")
            }
        }
    }
}