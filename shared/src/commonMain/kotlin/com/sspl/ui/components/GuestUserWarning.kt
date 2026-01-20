package com.sspl.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/02/2025.
 * se.muhammadimran@gmail.com
 */
@Composable
fun GuestUserWarning(modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(8))
            .background(color = MaterialTheme.colorScheme.error.copy(alpha = .15F))
            .padding(all = 12.dp)
    ) {
        AppTextTitle("Guest User")
        AppTextLabel("You are currently signed in as a Guest User. To mark attendance for certification, please sign up or log in using your official credentials.")
    }
}