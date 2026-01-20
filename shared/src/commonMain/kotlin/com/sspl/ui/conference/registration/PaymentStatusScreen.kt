package com.sspl.ui.conference.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.theme.windowBackGround
import com.sspl.ui.components.ButtonWithProgress

@Composable
fun PaymentStatusScreen(
    navController: NavController
) {
    val isSuccess = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean>("status") ?: true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(windowBackGround)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    color = if (isSuccess) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isSuccess) Color(0xFF4CAF50) else Color(0xFFF44336)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = if (isSuccess) "Successfully Payment Verified" else "Payment Verification Failed",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = if (isSuccess) Color(0xFF2E7D32) else Color(0xFFC62828)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isSuccess) 
                "Your registration for the conference has been confirmed." 
                else "We were unable to verify your payment. Please try again.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(64.dp))

        ButtonWithProgress(
            isEnabled = true,
            isLoading = false,
            buttonText = if (isSuccess) "GO TO HOME" else "TRY AGAIN",
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = {
                if (isSuccess) {
                    navController.navigate(Screen.HomeScreen.route) {
                        popUpTo(Screen.HomeScreen.route) { inclusive = true }
                    }
                } else {
                    navController.popBackStack()
                }
            }
        )
    }
}
