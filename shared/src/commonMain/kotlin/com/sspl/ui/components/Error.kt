package com.sspl.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sspl.core.models.ApiError

@Composable
fun Error(modifier: Modifier, error: ApiError) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppTextLabel(
            text = "Error",
        )
        AppTextBody(
            text = error.message,
        )
    }
}