package com.sspl.ui.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.usecases.EmailValidationUseCase
import com.sspl.core.usecases.InitResetPasswordUseCase
import com.sspl.core.usecases.PasswordValidationUseCase
import com.sspl.core.usecases.UpdatePasswordUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val emailValidationUseCase: EmailValidationUseCase,
    private val initResetPasswordUseCase: InitResetPasswordUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState = _uiState.asStateFlow()

    private val _initPasswordStatus = MutableSharedFlow<ApiStates<Any>>()
    val initPasswordStatus: SharedFlow<ApiStates<Any>> = _initPasswordStatus.asSharedFlow()

    private val _updatePasswordStatus = MutableSharedFlow<ApiStates<Any>>()
    val updatePasswordStatus: SharedFlow<ApiStates<Any>> = _updatePasswordStatus.asSharedFlow()

    fun onEvent(event: ForgotPasswordUiEvents) {
        when (event) {
            ForgotPasswordUiEvents.InitPassword -> {
                viewModelScope.launch {
                    initResetPasswordUseCase(_uiState.value.emailText).collect { state ->
                        _uiState.update { it.copy(isLoadingInitPassword = state is ApiStates.Loading) }

                        var isHandled = false
                        if (state is ApiStates.Failure) {
                            val errorMsg = state.error.message
                            if (errorMsg.contains(
                                    "Unauthorized",
                                    ignoreCase = true
                                )
                            ) {
                                _uiState.update {
                                    it.copy(
                                        emailError = "This email is not registered with us."
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
                            _initPasswordStatus.emit(state)
                        }

                        if (state is ApiStates.Success) {
                            _uiState.update {
                                it.copy(
                                    showOtpView = true, passwordToken = state.data?.token
                                )
                            }
                        }
                    }
                }
            }

            is ForgotPasswordUiEvents.EmailChanged -> {
                _uiState.update {
                    it.copy(
                        emailText = event.email, emailError = null
                    )
                }
                validateEmail()
            }

            is ForgotPasswordUiEvents.NewPasswordChanged -> {
                val isValid = passwordValidationUseCase(event.password)
                _uiState.update {
                    it.copy(
                        newPassword = event.password,
                        newPasswordError = if (!isValid) "Password must be at least 8 characters long" else null
                    )
                }
                updateOtpPasswordButtonState()

            }

            is ForgotPasswordUiEvents.ConfirmPasswordChanged -> {
                _uiState.update {
                    it.copy(
                        confirmPassword = event.password,
                        confirmPasswordError = if (event.password != _uiState.value.newPassword) "Passwords do not match" else null
                    )
                }
                updateOtpPasswordButtonState()

            }

            ForgotPasswordUiEvents.TogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !_uiState.value.isPasswordVisible) }
            }

            ForgotPasswordUiEvents.ToggleReenteredPasswordVisibility -> {
                _uiState.update { it.copy(isReenteredPasswordVisible = !_uiState.value.isReenteredPasswordVisible) }
            }

            is ForgotPasswordUiEvents.OnOtpEntered -> {
                _uiState.update {
                    it.copy(
                        otpString = event.otp, isOtpValid = event.otp.length == 6
                    )
                }
                updateOtpPasswordButtonState()
            }

            ForgotPasswordUiEvents.UpdatePassword -> {
                viewModelScope.launch {
                    updatePasswordUseCase(
                        _uiState.value.otpString,
                        _uiState.value.newPassword,
                        _uiState.value.passwordToken.orEmpty()
                    ).collect { state ->
                        _uiState.update { it.copy(isLoadingUpdatePassword = state is ApiStates.Loading) }
                        _updatePasswordStatus.emit(state)
                    }
                }
            }
        }
    }

    fun switchView(showOtpView: Boolean){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showOtpView = showOtpView
                )
            }
        }
    }
    private fun validateEmail() {
        val isEmailValid = emailValidationUseCase(_uiState.value.emailText)
        _uiState.update {
            it.copy(
                emailError = if (!isEmailValid) "Invalid email address" else null,
                isEnabledInitPassword = isEmailValid && _uiState.value.emailError == null && _uiState.value.emailText.isNotBlank(),  // Enable login only if both email and password are valid
            )
        }
    }

    private fun updateOtpPasswordButtonState() {

        _uiState.update {
            it.copy(
                isEnabledUpdatePassword = it.otpString.isNotBlank() && it.otpString.length > 5 && it.newPassword.isNotBlank() && it.confirmPassword.isNotBlank() && it.newPasswordError == null && it.confirmPasswordError == null
            )
        }
    }


}

data class ForgotPasswordUiState(
    val emailText: String = "",
    val emailError: String? = null,
    val isLoadingInitPassword: Boolean = false,
    val isEnabledInitPassword: Boolean = false,
    val isLoadingUpdatePassword: Boolean = false,
    val isEnabledUpdatePassword: Boolean = false,
    val passwordToken: String? = null,
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: String? = null,
    val otpString: String = "",
    val isOtpValid: Boolean = false,
    val confirmPasswordError: String? = null,
    val isPasswordVisible: Boolean = false,
    val isReenteredPasswordVisible: Boolean = false,
    val showOtpView: Boolean = false
)

sealed interface ForgotPasswordUiEvents {
    data class EmailChanged(val email: String) : ForgotPasswordUiEvents
    data object InitPassword : ForgotPasswordUiEvents
    data class OnOtpEntered(val otp: String) : ForgotPasswordUiEvents
    data class NewPasswordChanged(val password: String) : ForgotPasswordUiEvents
    data class ConfirmPasswordChanged(val password: String) : ForgotPasswordUiEvents
    data object TogglePasswordVisibility : ForgotPasswordUiEvents
    data object ToggleReenteredPasswordVisibility : ForgotPasswordUiEvents
    data object UpdatePassword : ForgotPasswordUiEvents
}