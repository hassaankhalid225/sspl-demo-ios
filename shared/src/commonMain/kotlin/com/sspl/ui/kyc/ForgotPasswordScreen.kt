package com.sspl.ui.kyc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.resources.Res
import com.sspl.resources.app_logo
import com.sspl.resources.continue_str
import com.sspl.resources.reset_password
import com.sspl.resources.enter_otp_below
import com.sspl.resources.enter_email_to_reset_password
import com.sspl.resources.enter_otp
import com.sspl.resources.enter_otp_sent_to_email
import com.sspl.resources.enter_your_mail
import com.sspl.resources.login_screen_background
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.ButtonWithProgress
import com.sspl.ui.components.PasswordTextField
import com.sspl.ui.otp.OtpEvent
import com.sspl.ui.otp.OtpView
import com.sspl.utils.IconResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 08/02/2025.
 * se.muhammadimran@gmail.com
 */
@Composable
fun ForgotPasswordScreen(
    navController: NavController, viewModel: ForgotPasswordViewModel = koinViewModel()
) {
    val scaffoldState = remember { SnackbarHostState() }


    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //status for either the code was sent to user or not
    LaunchedEffect(Unit) {
        viewModel.initPasswordStatus.collect { event ->
            when (event) {
                is ApiStates.Failure -> {
                    scaffoldState.showSnackbar(event.error.message)
                }

                else -> Unit
            }
        }
    }
    //status for tracking new password & otp
    LaunchedEffect(Unit) {
        viewModel.updatePasswordStatus.collect { event ->
            when (event) {
                is ApiStates.Failure -> {
                    scaffoldState.showSnackbar(event.error.message)
                }

                is ApiStates.Success -> {
                    navController.navigate(Screen.SignInScreen.route)
                }

                else -> Unit
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(scaffoldState) }) { _ ->
        Box(Modifier.fillMaxSize()) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(Res.drawable.login_screen_background),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.15f).background(
                    brush = Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = .8F),
                            MaterialTheme.colorScheme.primary.copy(alpha = .5F),
                            MaterialTheme.colorScheme.primary.copy(alpha = .1F),
                            Color.Transparent
                        )
                    )
                )
            ) {
                IconButton(
                    modifier = Modifier.padding(top = 8.dp).zIndex(2f),
                    onClick = {
                        if (uiState.showOtpView) {
                            viewModel.switchView(false)
                        } else {
                            navController.popBackStack()
                        }
                    },
                    colors = IconButtonDefaults.iconButtonColors()
                        .copy(contentColor = MaterialTheme.colorScheme.onPrimaryContainer)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, top = 48.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(!uiState.showOtpView) {
                    ForgotPasswordContent(
                        email = uiState.emailText,
                        emailErrorMsg = uiState.emailError,
                        isLoading = uiState.isLoadingInitPassword,
                        isEnabled = uiState.isEnabledInitPassword,
                        onEvents = viewModel::onEvent
                    )
                }
                AnimatedVisibility(uiState.showOtpView) {
                    OtpScreenContent(
                        otp = uiState.otpString,
                        password = uiState.newPassword,
                        passwordErrorMsg = uiState.newPasswordError,
                        rePassword = uiState.confirmPassword,
                        rePasswordErrorMsg = uiState.confirmPasswordError,
                        isLoadingUpdatePassword = uiState.isLoadingUpdatePassword,
                        isEnabledUpdatePassword = uiState.isEnabledUpdatePassword,
                        isPasswordVisible = uiState.isPasswordVisible,
                        isReenteredPasswordVisible = uiState.isReenteredPasswordVisible,
                        isOtpValid = uiState.isOtpValid,
                        onEvents = viewModel::onEvent
                    )
                }
            }
        }

    }
}

@Composable
private fun ForgotPasswordContent(
    email: String,
    emailErrorMsg: String? = null,
    isLoading: Boolean,
    isEnabled: Boolean,
    onEvents: (ForgotPasswordUiEvents) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Section
        Spacer(Modifier.weight(0.15f))
        HeaderSection(
            iconResource = IconResource.fromDrawableResource(drawable = Res.drawable.app_logo),
            title = stringResource(Res.string.reset_password),
            subTitle = stringResource(Res.string.enter_email_to_reset_password)
        )
        Spacer(Modifier.weight(0.15f))
        AnimatedVisibility(visible = true) {
            EmailInputField(email = email, emailErrorMsg = emailErrorMsg, onEmailChange = {
                onEvents.invoke(ForgotPasswordUiEvents.EmailChanged(it))
            })
        }
        Spacer(Modifier.height(22.dp))
        ButtonWithProgress(
            isEnabled = isEnabled,
            isLoading = isLoading,
            buttonText = stringResource(Res.string.continue_str),
        ) {
            onEvents(ForgotPasswordUiEvents.InitPassword)
        }
        Spacer(Modifier.weight(1f))

    }
}

@Composable
private fun OtpScreenContent(
    otp: String,
    password: String,
    passwordErrorMsg: String? = null,
    rePassword: String,
    rePasswordErrorMsg: String? = null,
    isLoadingUpdatePassword: Boolean,
    isEnabledUpdatePassword: Boolean, isOtpValid: Boolean,
    isPasswordVisible: Boolean,
    isReenteredPasswordVisible: Boolean,
    onEvents: (ForgotPasswordUiEvents) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Section
        Spacer(Modifier.weight(0.15f))
        HeaderSection(
            iconResource = IconResource.fromDrawableResource(drawable = Res.drawable.app_logo),
            title = stringResource(Res.string.enter_otp),
            subTitle = stringResource(Res.string.enter_otp_sent_to_email)
        )
        Spacer(Modifier.weight(0.15f))
        AppTextSubTitle(stringResource(Res.string.enter_otp_below))
        OtpView(
            otpLength = 6,
            otpValue = otp,
            onOtpChange = { onEvents(ForgotPasswordUiEvents.OnOtpEntered(it)) },
            onOtpEntered = { })
        PasswordTextField(
            text = password,
            isEnabled = isOtpValid,
            errorMessage = passwordErrorMsg,
            isPasswordVisible = isPasswordVisible,
            onTextChange = {
                onEvents.invoke(ForgotPasswordUiEvents.NewPasswordChanged(it))
            },
            onTogglePassword = {
                onEvents.invoke(ForgotPasswordUiEvents.TogglePasswordVisibility)
            },
        )
        PasswordTextField(
            text = rePassword,
            isEnabled = isOtpValid,
            errorMessage = rePasswordErrorMsg,
            isPasswordVisible = isReenteredPasswordVisible,
            onTextChange = {
                onEvents.invoke(ForgotPasswordUiEvents.ConfirmPasswordChanged(it))
            },
            onTogglePassword = {
                onEvents.invoke(ForgotPasswordUiEvents.ToggleReenteredPasswordVisibility)
            },
        )

        Spacer(Modifier.height(22.dp))
        ButtonWithProgress(
            isEnabled = isEnabledUpdatePassword,
            isLoading = isLoadingUpdatePassword,
            buttonText = stringResource(Res.string.reset_password),
        ) {
            onEvents(ForgotPasswordUiEvents.UpdatePassword)
        }
        Spacer(Modifier.weight(1f))

    }
}

@Composable
private fun EmailInputField(
    email: String, emailErrorMsg: String?, onEmailChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        AppTextField(
            placeholder = stringResource(Res.string.enter_your_mail),
            modifier = Modifier.fillMaxWidth(),
            text = email,
            errorMessage = emailErrorMsg,
            isError = emailErrorMsg != null,
            onTextChange = onEmailChange
        )
    }
}