package com.sspl.ui.kyc

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.resources.Res
import com.sspl.resources.app_logo
import com.sspl.resources.create_new_account
import com.sspl.resources.enter_your_mail
import com.sspl.resources.enter_your_password
import com.sspl.resources.forgot_password
import com.sspl.resources.get_updates
import com.sspl.resources.login
import com.sspl.resources.login_here
import com.sspl.resources.login_screen_background
import com.sspl.resources.continue_as_guest
import com.sspl.resources.sign_in_via_phone
import com.sspl.theme.primary
import com.sspl.ui.components.AppRoundedCornerButton
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSmall
import com.sspl.ui.components.ButtonWithProgress
import com.sspl.ui.components.PasswordTextField
import com.sspl.ui.components.PhoneWithCountrySelector
import com.sspl.ui.components.countrypicker.Country
import com.sspl.ui.feedback.FeedbackScreenContent
import com.sspl.utils.IconResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 09/01/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.oneTimeEvents.collect { event ->
            when (event) {
                OneTimeEvents.NavigateToOtpScreen -> {
                    navController.navigate(Screen.OtpScreen.route)
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.networkState.collect { result ->
            when (result) {
                is ApiStates.Failure -> scaffoldState.showSnackbar(result.error.message)
                is ApiStates.Success -> {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.SignInScreen.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }

                else -> Unit
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(scaffoldState) }) { padding ->

        LoginScreenContent(
            email = uiState.email,
            password = uiState.password,
            phone = uiState.phoneNo,
            selectedCountry = uiState.country,
            isOtpMode = uiState.isOtpMode,
            showPassword = uiState.isPasswordVisible,
            emailErrorMsg = uiState.emailError,
            passwordErrorMsg = uiState.passwordError,
            isLoginEnabled = uiState.isLoginEnabled,
            phoneErrorMsg = uiState.phoneNoError,
            isLoading = uiState.isLoading, showCountryChooser = uiState.showCountryChooser,
            onEvents = viewModel::onEvent,
            createNewAccount = {
                navController.navigate(Screen.SignUpScreen.route)
            },
            onForgotPassword = {
                 navController.navigate(Screen.ForgotPasswordScreen.route)
             },
            onExhibitionClick = {
                navController.navigate(Screen.ExhibitionRegistrationScreen.route)
            },
            modifier = modifier
        )

    }


}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    onEvents: (LoginScreenUiEvents) -> Unit = {},
    emailErrorMsg: String?,
    passwordErrorMsg: String?,
    isLoginEnabled: Boolean = false,
    phoneErrorMsg: String?,
    isLoading: Boolean = true,
    selectedCountry: Country,
    showCountryChooser: Boolean = false,
    email: String = "",
    phone: String = "",
    password: String = "",
    createNewAccount: () -> Unit,
    showPassword: Boolean = false,
    onForgotPassword: () -> Unit = {},
    isOtpMode: Boolean = false,
    onExhibitionClick: () -> Unit = {}
) {
    Box(modifier.fillMaxSize()) {
        Image(
            modifier= Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.login_screen_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f)
                .background(
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
        )
        Column(
            modifier = modifier
                .fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title Section
            Spacer(Modifier.weight(0.15f))
            HeaderSection(
                iconResource = IconResource.fromDrawableResource(drawable = Res.drawable.app_logo),
                title = stringResource(Res.string.login_here),
                subTitle = stringResource(Res.string.get_updates)
            )
            Spacer(Modifier.weight(0.15f))

            // Form Section (Email/Password or Phone)
            AnimatedVisibility(visible = !isOtpMode) {
                LoginFields(
                    modifier = modifier,
                    email = email,
                    emailErrorMsg = emailErrorMsg,
                    password = password,
                    passwordErrorMsg = passwordErrorMsg,
                    showPassword = showPassword,
                    onEmailChange = { onEvents(LoginScreenUiEvents.OnEmailTextChange(it)) },
                    onPasswordChange = { onEvents(LoginScreenUiEvents.OnPasswordTextChange(it)) },
                    onPasswordToggle = { onEvents(LoginScreenUiEvents.TogglePasswordVisibility) },
                    onForgotPassword = onForgotPassword
                )
            }

            AnimatedVisibility(visible = isOtpMode) {
                PhoneWithCountrySelector(
                    selectedCountry = selectedCountry,
                    errorMessage = phoneErrorMsg,
                    phone = phone,
                    onPhoneNumberChange = { onEvents(LoginScreenUiEvents.OnPhoneNoChange(it)) },
                    showCountryChooser = showCountryChooser,
                    onCountryChange = { onEvents(LoginScreenUiEvents.OnCountryChange(it)) },
                    toggleCountryPicker = { onEvents(LoginScreenUiEvents.ToggleCountryChooser) }
                )
            }

            // Login Button
            Spacer(modifier = Modifier.height(10.dp))
            ButtonWithProgress(
                isEnabled = isLoginEnabled,
                isLoading = isLoading,
                buttonText = stringResource(Res.string.login),
                modifier = Modifier.imePadding()
            ) {
                onEvents(LoginScreenUiEvents.OnSave)
            }
            // Create Account Section
            Spacer(modifier = Modifier.height(10.dp))
            AppTextLabel(
                text = stringResource(Res.string.create_new_account),
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { createNewAccount() }
            )

            // Divider with "or" text
            Spacer(modifier = Modifier.height(10.dp))
            DividerWithText("or")

            // Exhibition Registration Button (Prominent)
            Spacer(modifier = Modifier.height(10.dp))
            ButtonWithProgress(
                isEnabled = true,
                isLoading = false,
                buttonText = "Exhibition Registration",
                modifier = Modifier.imePadding()
            ) {
                onExhibitionClick()
            }

            // Continue As Guest Button
            ButtonWithProgress(
                isEnabled = true,
                isLoading = isLoading,
                buttonText = stringResource(Res.string.continue_as_guest),
                modifier = Modifier.imePadding()
            ) {
                onEvents(LoginScreenUiEvents.ContinueAsGuest)
            }
            Spacer(Modifier.weight(0.2f))
        }
    }
}

@Composable
fun LoginFields(
    modifier: Modifier = Modifier,
    email: String,
    emailErrorMsg: String?,
    password: String,
    passwordErrorMsg: String?,
    showPassword: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordToggle: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        AppTextField(
            placeholder = stringResource(Res.string.enter_your_mail),
            modifier = Modifier.fillMaxWidth(),
            text = email,
            errorMessage = emailErrorMsg,
            isError = emailErrorMsg != null,
            onTextChange = onEmailChange
        )

        PasswordTextField(
            placeholder = stringResource(Res.string.enter_your_password),
            modifier = Modifier.fillMaxWidth(),
            text = password,
            errorMessage = passwordErrorMsg,
            isError = passwordErrorMsg != null,
            onTogglePassword = onPasswordToggle,
            isPasswordVisible = showPassword,
            onTextChange = onPasswordChange
        )

        AppTextSmall(
            text = stringResource(Res.string.forgot_password),
            color = primary,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 12.sp,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.align(Alignment.End).clickable { onForgotPassword() }
        )
    }
}

@Composable
fun DividerWithText(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        AppTextSmall(text)
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
