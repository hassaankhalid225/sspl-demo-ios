package com.sspl.ui.kyc


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.sspl.resources.already_have_an_account
import com.sspl.resources.app_logo
import com.sspl.resources.enter_first_name
import com.sspl.resources.enter_last_name
import com.sspl.resources.enter_your_mail
import com.sspl.resources.enter_your_password
import com.sspl.resources.login
import com.sspl.resources.login_screen_background
import com.sspl.resources.sign_up
import com.sspl.ui.components.AppTextField
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.ButtonWithProgress
import com.sspl.ui.components.PasswordTextField
import com.sspl.ui.components.PhoneWithCountrySelector
import com.sspl.ui.components.countrypicker.Country
import com.sspl.utils.IconResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SignUpScreen(
    navController: NavController, viewModel: SignUpViewModel = koinViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scaffoldState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        viewModel.networkState.collect { event ->
            when (event) {
                is ApiStates.Failure -> {
                    scaffoldState.showSnackbar(event.error.message)
                }

                is ApiStates.Success -> {
                    navController.navigate(Screen.UserDetailsScreen.route){
                        popUpTo(Screen.SignUpScreen.route){
                            inclusive = true
                        }
                        popUpTo(Screen.SignInScreen.route){
                            inclusive = true
                        }
                    }
                }

                else -> Unit
            }
        }
    }
    Scaffold(snackbarHost = { SnackbarHost(scaffoldState) }) { _ ->
        SignUpScreenContent(
            modifier = Modifier,
            email = uiState.email,
            isSignUpEnabled = uiState.isSignUpEnabled,
            firstName = uiState.firstName,
            lastName = uiState.lastName,
            reEnterPassword = uiState.retypedPassword,
            reEnterPasswordErrorMsg = uiState.retypedPasswordError,
            isReEnteredVisible = uiState.isReenteredPasswordVisible,
            isLoading = uiState.isLoading,
            passwordErrorMsg = uiState.passwordError,
            phoneError = uiState.phoneNoError,
            password = uiState.password,
            isPasswordVisible = uiState.isPasswordVisible,
            showCountryChooser = uiState.showCountryChooser,
            country = uiState.country,
            emailErrorMsg = uiState.emailError, firstNameErrorMsg = uiState.firstNameError,
            lastNameErrorMsg = uiState.lastNameError,
            phoneNo = uiState.phoneNo,
            loginScreen = {
                navController.navigate(Screen.SignInScreen.route)
            },
            onEvent = viewModel::onEvent,
        )

    }
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    email: String,
    isSignUpEnabled: Boolean,
    firstName: String,
    lastName: String,
    reEnterPassword: String,
    reEnterPasswordErrorMsg: String?,
    isReEnteredVisible: Boolean,
    isLoading: Boolean,
    passwordErrorMsg: String?,
    firstNameErrorMsg: String?,
    lastNameErrorMsg: String?,
    phoneError: String?,
    password: String,
    isPasswordVisible: Boolean,
    showCountryChooser: Boolean,
    country: Country,
    emailErrorMsg: String?,
    phoneNo: String,
    loginScreen: () -> Unit,
    onEvent: (UserEvents) -> Unit
) {
    Box(modifier.fillMaxSize()) {
        Image(
            modifier= Modifier.fillMaxSize(),
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
        )

        Column(
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.15f))
            HeaderSection(
                iconResource = IconResource.fromDrawableResource(drawable = Res.drawable.app_logo)
            )
            Spacer(Modifier.weight(0.15f))
            // Input Fields Section
            InputFieldsSection(
                firstName = firstName,
                lastName = lastName,
                reEnterPassword = reEnterPassword,
                reEnterPasswordErrorMsg = reEnterPasswordErrorMsg,
                isReEnteredVisible = isReEnteredVisible,
                passwordErrorMsg = passwordErrorMsg,
                firstNameErrorMsg = firstNameErrorMsg,
                lastNameErrorMsg = lastNameErrorMsg,
                password = password,
                phoneError = phoneError,
                isPasswordVisible = isPasswordVisible,
                country = country,
                email = email,
                emailErrorMsg = emailErrorMsg,
                phoneNo = phoneNo,
                showCountryChooser = showCountryChooser,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(10.dp))

            ButtonWithProgress(
                isEnabled = isSignUpEnabled,
                isLoading = isLoading,
                buttonText = stringResource(Res.string.sign_up),
                modifier = Modifier.imePadding()
            ) {
                onEvent(UserEvents.Submit)
            }

            AlreadyHaveAccountSection(loginScreen)

            Spacer(Modifier.weight(0.7f))
        }
    }
}


@Composable
fun InputFieldsSection(
    firstName: String,
    lastName: String,
    reEnterPassword: String,
    reEnterPasswordErrorMsg: String?,
    isReEnteredVisible: Boolean,
    passwordErrorMsg: String?,
    firstNameErrorMsg: String?,
    lastNameErrorMsg: String?,
    password: String,
    isPasswordVisible: Boolean,
    country: Country,
    email: String,
    emailErrorMsg: String?,
    phoneError: String?,
    phoneNo: String,
    showCountryChooser: Boolean,
    onEvent: (UserEvents) -> Unit
) {
    AppTextField(placeholder = stringResource(Res.string.enter_first_name),
        text = firstName,
        errorMessage = firstNameErrorMsg,
        onTextChange = { onEvent(UserEvents.SetFirstName(it)) })

    AppTextField(placeholder = stringResource(Res.string.enter_last_name),
        text = lastName,
        errorMessage = lastNameErrorMsg,
        onTextChange = { onEvent(UserEvents.SetLastName(it)) })

    PhoneWithCountrySelector(selectedCountry = country,
        phone = phoneNo, errorMessage = phoneError,
        onPhoneNumberChange = { onEvent(UserEvents.SetPhoneNo(it)) },
        showCountryChooser = showCountryChooser,
        onCountryChange = { onEvent(UserEvents.OnCountryChange(it)) },
        toggleCountryPicker = { onEvent(UserEvents.ToggleCountryChooser) })

    AppTextField(placeholder = stringResource(Res.string.enter_your_mail),
        text = email,
        errorMessage = emailErrorMsg,
        onTextChange = { onEvent(UserEvents.SetEmail(it)) })

    PasswordTextField(placeholder = stringResource(Res.string.enter_your_password),
        text = password,
        errorMessage = passwordErrorMsg,
        isPasswordVisible = isPasswordVisible,
        onTogglePassword = { onEvent(UserEvents.TogglePasswordVisibility) },
        onTextChange = { onEvent(UserEvents.SetPassword(it)) })

    PasswordTextField(placeholder = stringResource(Res.string.enter_your_password),
        text = reEnterPassword,
        errorMessage = reEnterPasswordErrorMsg,
        isPasswordVisible = isReEnteredVisible,
        onTogglePassword = { onEvent(UserEvents.ToggleReenteredPasswordVisibility) },
        onTextChange = { onEvent(UserEvents.OnRetypePassword(it)) })
}

@Composable
fun AlreadyHaveAccountSection(loginScreen: () -> Unit) {
    AppTextLabel(text = stringResource(Res.string.already_have_an_account),
        fontSize = 12.sp,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { loginScreen() })
}

