package com.sspl.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun PaymentWebView(
    url: String,
    onPaymentSuccess: () -> Unit,
    onPaymentFailed: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
)
