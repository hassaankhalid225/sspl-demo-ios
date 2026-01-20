package com.sspl.platform.shared

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 30/01/2025.
 * se.muhammadimran@gmail.com
 */

actual class UrlOpener(private val context: Context) {
    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}