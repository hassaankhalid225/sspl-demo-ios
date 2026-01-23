package com.sspl.ui.conference.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.models.Conference
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.primary
import com.sspl.theme.windowBackGround
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.ButtonWithProgress
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConferenceRegistrationScreen(
    navController: NavController,
    viewModel: ConferenceRegistrationViewModel = koinViewModel()
) {
    val dataString = navController.previousBackStackEntry?.savedStateHandle?.get<String>("data")
    val conferenceArg = remember(dataString) {
        dataString?.let { Json.decodeFromString<Conference>(it) }
    }

    LaunchedEffect(conferenceArg) {
        conferenceArg?.let { viewModel.loadData(it.id) }
    }

    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is RegistrationUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is RegistrationUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${state.message}")
            }
        }
        is RegistrationUiState.Content -> {
            ConferenceRegistrationContent(
                state = state,
                viewModel = viewModel,
                navController = navController,
                initialTitle = conferenceArg?.title
            )
        }
    }
}

@Composable
fun ConferenceRegistrationContent(
    state: RegistrationUiState.Content,
    viewModel: ConferenceRegistrationViewModel,
    navController: NavController,
    initialTitle: String?
) {
    val roles = state.roles
    
    // Auto-select role if registered
    var selectedRoleIndex by remember(state.isRegistered, state.registration) { 
        val registeredRoleId = state.registration?.roleId
        val index = if (registeredRoleId != null) {
            roles.indexOfFirst { it.id == registeredRoleId }.takeIf { it >= 0 } ?: 0
        } else 0
        mutableStateOf(index)
    }
    
    var isExpanded by remember { mutableStateOf(false) }

    // Payment WebView Dialog
    if (state.paymentUrl != null) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.onPaymentUrlOpened() },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
             com.sspl.ui.components.PaymentWebView(
                 url = state.paymentUrl,
                 onPaymentSuccess = {
                     viewModel.onPaymentUrlOpened()
                     viewModel.loadData(state.details.id) // Refresh status
                 },
                 onPaymentFailed = {
                     viewModel.onPaymentUrlOpened()
                 },
                 onClose = { viewModel.onPaymentUrlOpened() },
                 modifier = Modifier.fillMaxSize()
             )
        }
    }

    // If payment succeeds (status updated after refresh), navigate
    LaunchedEffect(state.registration?.paymentStatus) {
        if (state.registration?.paymentStatus == "SUCCESS") {
            navController.currentBackStackEntry?.savedStateHandle?.set("status", true)
            navController.navigate(Screen.PaymentStatusScreen.route)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(windowBackGround)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Conference Name
        Column {
            AppTextLabel(text = "Conference Name", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = boxColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = initialTitle ?: state.details.title ?: "Unknown Conference",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (roles.isNotEmpty()) {
            // Role and Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Role Dropdown
                Column(modifier = Modifier.weight(1f)) {
                    AppTextLabel(text = "Role", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { if (!state.isRegistered) isExpanded = !isExpanded }, // Disable change if registered
                            colors = CardDefaults.cardColors(containerColor = columnColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = roles.getOrNull(selectedRoleIndex)?.name ?: "Select Role",
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                if (!state.isRegistered) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                        }
                        DropdownMenu(
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.6f)
                        ) {
                            roles.forEachIndexed { index, role ->
                                DropdownMenuItem(
                                    text = { Text(role.name) },
                                    onClick = {
                                        selectedRoleIndex = index
                                        isExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Price (Non-editable)
                Column(modifier = Modifier.weight(0.5f)) {
                    AppTextLabel(text = "Price", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = boxColor.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${roles.getOrNull(selectedRoleIndex)?.fee ?: 0}",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = primary
                        )
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.LightGray.copy(alpha = 0.5f))

        state.error?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Action Button
        val isRegistered = state.isRegistered
        val paymentStatus = state.registration?.paymentStatus
        
        val buttonText = when {
            !isRegistered -> "REGISTER"
            paymentStatus == "PENDING" || paymentStatus == "FAILED" -> "PAY NOW"
            paymentStatus == "SUCCESS" -> "PAID"
            else -> "REGISTERED"
        }
        
        val isButtonEnabled = when {
             state.isLoading -> false
             !isRegistered -> roles.isNotEmpty()
             paymentStatus == "PENDING" || paymentStatus == "FAILED" -> true
             else -> false // Disable if already paid
        }

        ButtonWithProgress(
            isEnabled = isButtonEnabled,
            isLoading = state.isLoading,
            buttonText = buttonText,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            onClick = {
                if (!isRegistered) {
                    roles.getOrNull(selectedRoleIndex)?.let { role ->
                        viewModel.register(role.id)
                    }
                } else if (paymentStatus == "PENDING" || paymentStatus == "FAILED") {
                    viewModel.initiatePayment()
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
