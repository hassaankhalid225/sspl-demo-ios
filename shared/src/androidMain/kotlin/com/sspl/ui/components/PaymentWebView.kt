package com.sspl.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun PaymentWebView(
    url: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier
) {
    val context = LocalContext.current
    
    LaunchedEffect(url) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            // Reset the state so the dialog closes and the browser handles the rest
            onClose()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Opening Secure Payment Gateway...")
    }
}
