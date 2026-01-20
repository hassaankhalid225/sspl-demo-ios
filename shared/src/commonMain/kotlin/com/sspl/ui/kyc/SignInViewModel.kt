package com.sspl.ui.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.UserResponse
import com.sspl.core.usecases.EmailValidationUseCase
import com.sspl.core.usecases.LoginUserUseCase
import com.sspl.core.usecases.PasswordValidationUseCase
import com.sspl.core.usecases.PhoneNumberValidationUseCase
import com.sspl.core.usecases.SignUpUserUseCase
import com.sspl.utils.EMAIL_GUEST_USER
import com.sspl.utils.PASSWORD_GUEST_USER
import com.sspl.utils.PHONE_GUEST_USER
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import com.sspl.utils.DateTimeUtils

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 09/01/2025.
 * se.muhammadimran@gmail.com
 */
class SignInViewModel(
    private val emailValidationUseCase: EmailValidationUseCase,
    private val phoneNumberValidationUseCase: PhoneNumberValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val loginUserUseCase: LoginUserUseCase,
    private val signUpUseCase: SignUpUserUseCase,

    ) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val _oneTimeEvents = MutableSharedFlow<OneTimeEvents>()
    val oneTimeEvents = _oneTimeEvents.asSharedFlow()


    private val _networkState = MutableSharedFlow<ApiStates<UserResponse>>()
    val networkState: SharedFlow<ApiStates<UserResponse>> = _networkState.asSharedFlow()


    fun onEvent(event: LoginScreenUiEvents) {
        when (event) {
            is LoginScreenUiEvents.OnEmailTextChange -> {
                _uiState.update {
                    it.copy(
                        email = event.emailText, emailError = null
                    )
                }
                validateEmail()
            }

            is LoginScreenUiEvents.OnPasswordTextChange -> {
                _uiState.update {
                    it.copy(
                        password = event.passwordText, passwordError = null
                    )
                }
                validatePassword()
            }

            LoginScreenUiEvents.OnSave -> handleSave()
            LoginScreenUiEvents.TogglePasswordVisibility -> _uiState.update {
                it.copy(isPasswordVisible = !it.isPasswordVisible)
            }

            LoginScreenUiEvents.ToggleOtpMode -> _uiState.update {
                it.copy(
                    isOtpMode = !it.isOtpMode,
                    phoneNo = "",
                    email = "",
                    isLoginEnabled = false,
                    password = ""
                )
            }

            is LoginScreenUiEvents.OnPhoneNoChange -> {
                _uiState.update {
                    it.copy(phoneNo = event.phoneNumber)
                }
                validatePhoneNumber()
            }

            is LoginScreenUiEvents.OnCountryChange -> _uiState.update {
                it.copy(
                    country = event.country, showCountryChooser = false
                )
            }

            LoginScreenUiEvents.ToggleCountryChooser -> _uiState.update {
                it.copy(showCountryChooser = !it.showCountryChooser)
            }

            LoginScreenUiEvents.ContinueAsGuest -> {
                viewModelScope.launch {
                    continueAsGuest()
                }
            }


        }
    }

    private fun validatePassword() {
        val isPasswordValid = passwordValidationUseCase(_uiState.value.password)
        _uiState.update {
            it.copy(
                passwordError = if (!isPasswordValid) "Password must be at least 6 characters long" else null,
                isLoginEnabled = isPasswordValid && _uiState.value.emailError == null && _uiState.value.email.isNotBlank(),  // Enable login only if both email and password are valid
            )
        }
    }

    private fun validateEmail() {
        val isEmailValid = emailValidationUseCase(_uiState.value.email)
        _uiState.update {
            it.copy(
                emailError = if (!isEmailValid) "Invalid email address" else null,
                isLoginEnabled = isEmailValid && _uiState.value.passwordError == null && _uiState.value.password.isNotBlank(),  // Enable login only if both email and password are valid

            )
        }
    }

    private fun validatePhoneNumber() {
        val phoneNo = "${_uiState.value.country.dialCode.first()}${_uiState.value.phoneNo}"

        val isPhoneValid = phoneNumberValidationUseCase(phoneNo)
        _uiState.update {
            it.copy(
                phoneNoError = if (!isPhoneValid) "Invalid phone number" else null,
                isLoginEnabled = isPhoneValid && _uiState.value.isOtpMode,  // Enable login only if phone number is valid in OTP mode

            )
        }
    }


    private fun handleSave() {
        viewModelScope.launch {
            if (_uiState.value.isOtpMode) {
                handleOtpSave()
            } else {
                handleEmailPasswordSave()
            }

        }
    }

    private suspend fun handleOtpSave() {
        val phoneNo = "${_uiState.value.country.dialCode.first()}${_uiState.value.phoneNo}"

        if (phoneNumberValidationUseCase(phoneNo)) {
            _oneTimeEvents.emit(OneTimeEvents.NavigateToOtpScreen)
        } else {
            _uiState.update { it.copy(phoneNoError = "Invalid phone number") }
        }
    }

    private suspend fun handleEmailPasswordSave() {
        val isEmailValid = emailValidationUseCase(_uiState.value.email)
        val isPasswordValid = passwordValidationUseCase(_uiState.value.password)

        if (!isEmailValid || !isPasswordValid) {
            if (!isEmailValid) {
                _uiState.update { it.copy(emailError = "Invalid email address") }
            }
            if (!isPasswordValid) {
                _uiState.update {
                    it.copy(
                        passwordError = "Password must be at least 8 characters long"
                    )
                }
            }
            return
        }
        logInViaEmailPassword(
            email = _uiState.value.email,
            password = _uiState.value.password
        )
    }

    private suspend fun logInViaEmailPassword(email: String, password: String) {
        loginUserUseCase.signInUserViaEmail(
            email = email, password = password
        ).collect { state ->
            _uiState.update { it.copy(isLoading = state is ApiStates.Loading) }

            var isHandled = false
            if (state is ApiStates.Failure) {
                val errorMsg = state.error.message
                if (errorMsg.contains(
                        "Unauthorized",
                        ignoreCase = true
                    ) || errorMsg.contains(
                        "Invalid credentials",
                        ignoreCase = true
                    )
                ) {
                    _uiState.update {
                        it.copy(
                            emailError = "Invalid email or password."
                        )
                    }
                    isHandled = true
                } else if (errorMsg.contains(
                        "Account does not exist",
                        ignoreCase = true
                    )
                ) {
                    _uiState.update {
                        it.copy(
                            emailError = "Account does not exist against this email address."
                        )
                    }
                    isHandled = true
                }
            }

            if (!isHandled) {
                _networkState.emit(state)
            }
        }
    }

    private fun continueAsGuest() {
        viewModelScope.launch {
            val email = "${DateTimeUtils.timeInMilliNow()}." + EMAIL_GUEST_USER
            signUpUseCase.signUpUser(
                firstName = "Guest",
                lastName = "User",
                email = email,
                password = PASSWORD_GUEST_USER,
                phoneNumber = PHONE_GUEST_USER
            ).collect { state ->
                _uiState.update { it.copy(isLoading = state is ApiStates.Loading) }
                _networkState.emit(state)
            }
        }
    }
}

