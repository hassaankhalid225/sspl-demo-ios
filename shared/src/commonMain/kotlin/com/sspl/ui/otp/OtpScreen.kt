package com.sspl.ui.otp

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.resources.Res
import com.sspl.resources.enter_otp_sent_to_your_number
import com.sspl.resources.resend_it_now
import com.sspl.resources.still_waiting_for_your_otp
import com.sspl.resources.verification_code
import com.sspl.ui.components.AppTextSmall
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.AppTextTitle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OtpScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: OtpViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scaffoldState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.resultFlow.collect { result ->
            when (result) {
                is OtpOneTimeEvents.ShowMessage -> {
                    scope.launch {
                        scaffoldState.showSnackbar(result.message)
                    }
                }

                OtpOneTimeEvents.Success -> {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.SignInScreen.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    OtpScreenScaffold(
        scaffoldState = scaffoldState,
        content = {
            OtpScreenContent(
                modifier = modifier,
                uiState = uiState,
                onEvent = remember { viewModel::onEvent }
            )
        }
    )
}

@Composable
fun OtpScreenScaffold(
    scaffoldState: SnackbarHostState,
    content: @Composable () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(scaffoldState) }
    ) { _ ->
        Box(modifier = Modifier) {
            content()
        }
    }
}

@Composable
fun OtpScreenContent(
    modifier: Modifier = Modifier,
    uiState: OtpState,
    onEvent: (OtpEvent) -> Unit
) {
    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        val contentAlpha by remember(uiState.isVerifying) {
            derivedStateOf { if (uiState.isVerifying) 0.3f else 1f }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AppTextTitle(
                text = stringResource(Res.string.verification_code),
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextSubTitle(
                text = stringResource(Res.string.enter_otp_sent_to_your_number),
            )

            Spacer(modifier = Modifier.height(24.dp))
            OtpView(
                otpLength = 6,
                otpValue = uiState.otpValue,
                onOtpChange = { onEvent(OtpEvent.OtpValueChanged(it)) },
                onOtpEntered = { onEvent(OtpEvent.VerifyOtp(it)) }
            )
            ResendOtpRow(
                isVerifying = uiState.isVerifying,
                onResendClick = { onEvent(OtpEvent.ResendOtp) }
            )
        }

        if (uiState.isVerifying) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ResendOtpRow(
    isVerifying: Boolean,
    onResendClick: () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        AppTextSmall(text = stringResource(Res.string.still_waiting_for_your_otp))
        AppTextSmall(
            text = stringResource(Res.string.resend_it_now),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(enabled = !isVerifying) {
                onResendClick()
            }
        )
    }
}


@Composable
fun OtpView(
    otpLength: Int = 6,
    otpValue: String,
    onOtpChange: (String) -> Unit,
    onOtpEntered: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(100) // Short delay to ensure the view is ready
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(otpLength) { index ->
                OtpDigit(
                    value = otpValue.getOrNull(index)?.toString() ?: "",
                    onClick = {
                        scope.launch {
                            focusManager.clearFocus(force = true)
                            delay(100)
                            focusRequester.requestFocus()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = otpValue,
            onValueChange = { newValue ->
                if (newValue.length <= otpLength) {
                    onOtpChange(newValue.filter { it.isDigit() })
                    if (newValue.length == otpLength) {
                        onOtpEntered(newValue)
                        focusManager.clearFocus() // Dismiss keyboard when all digits are entered
                    }
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .fillMaxWidth()
                .height(0.dp)
                .focusRequester(focusRequester)
        )
    }
}

@Composable
fun OtpDigit(value: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AppTextSubTitle(
            text = value,
        )
    }
}
