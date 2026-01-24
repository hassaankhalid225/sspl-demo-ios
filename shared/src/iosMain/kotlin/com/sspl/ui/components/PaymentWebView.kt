package com.sspl.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

@Composable
actual fun PaymentWebView(
    url: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier
) {
    LaunchedEffect(url) {
        val nsUrl = NSURL.URLWithString(url)
        if (nsUrl != null) {
            UIApplication.sharedApplication.openURL(nsUrl)
            // Reset the state so the dialog closes and the browser handles the rest
            onClose()
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Opening Secure Payment Gateway...")
    }
}
