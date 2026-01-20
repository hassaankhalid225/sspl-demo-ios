package com.sspl.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.ui.components.AppTextLabel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun SettingsScreen(navController: NavController) {
    Card(
        modifier = Modifier.fillMaxSize()
            .padding(all = 16.dp),
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                ).clickable {
                    navController.navigate(route = Screen.DeleteAccountScreen.route)
                }
                    .padding(all = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppTextLabel(text = "Delete account Permanently?")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = ""
                )
            }
        }

    }
}