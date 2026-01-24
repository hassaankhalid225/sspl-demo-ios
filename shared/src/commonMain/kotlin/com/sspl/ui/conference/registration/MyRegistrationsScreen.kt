package com.sspl.ui.conference.registration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sspl.ui.components.AppTextLabel
import com.sspl.core.models.Registration
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MyRegistrationsScreen(
    conferenceId: Long,
    viewModel: MyRegistrationsViewModel = koinViewModel()
) {
    LaunchedEffect(conferenceId) {
        viewModel.loadRegistrations(conferenceId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MyRegistrationsUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is MyRegistrationsUiState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is MyRegistrationsUiState.Success -> {
                RegistrationList(registrations = state.registrations)
            }
        }
    }
}

@Composable
fun RegistrationList(registrations: List<Registration>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(registrations) { registration ->
            RegistrationItem(registration)
        }
    }
}

@Composable
fun RegistrationItem(registration: Registration) {
    // Simple Item View
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxSize().padding(vertical = 8.dp),
    ) {
        androidx.compose.foundation.layout.Column(
             modifier = Modifier.padding(16.dp)
        ) {
            AppTextLabel(text = "Role: ${registration.role?.name ?: "Unknown"}")
            Text(text = "Status: ${registration.paymentStatus}")
            Text(text = "Fee: ${registration.feeAmount}")
            registration.paymentDate?.let {
                Text(text = "Date: $it")
            }
        }
    }
}
