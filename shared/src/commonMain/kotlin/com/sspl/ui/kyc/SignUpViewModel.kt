package com.sspl.ui.kyc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.usecases.EmailValidationUseCase
import com.sspl.core.usecases.PasswordValidationUseCase
import com.sspl.core.usecases.PhoneNumberValidationUseCase
import com.sspl.core.usecases.SignUpUserUseCase
import com.sspl.core.usecases.ValidateUserNameUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SignUpViewModel(
    private val emailValidationUseCase: EmailValidationUseCase,
    private val passwordValidationUseCase: PasswordValidationUseCase,
    private val nameValidationUseCase: ValidateUserNameUseCase,
    private val phoneValidationUseCase: PhoneNumberValidationUseCase,
    private val signUpUserUseCase: SignUpUserUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    private val _networkState = MutableSharedFlow<ApiStates<Any>>()
    val networkState: SharedFlow<ApiStates<Any>> = _networkState.asSharedFlow()


    fun onEvent(event: UserEvents) {
        viewModelScope.launch {
            when (event) {
                is UserEvents.SetEmail -> {
                    val isValid = emailValidationUseCase(event.email)
                    _uiState.update {
                        it.copy(
                            email = event.email,
                            emailError = if (!isValid) "Invalid email address" else null
                        )
                    }
                }

                is UserEvents.SetFirstName -> {
                    val validationResult = nameValidationUseCase(event.firstName)
                    _uiState.update {
                        it.copy(
                            firstName = event.firstName,
                            firstNameError = if (validationResult is ValidationResult.Error) validationResult.message else null
                        )
                    }
                }

                is UserEvents.SetLastName -> {
                    val validationResult = nameValidationUseCase(event.lastName)
                    _uiState.update {
                        it.copy(
                            lastName = event.lastName,
                            lastNameError = if (validationResult is ValidationResult.Error) validationResult.message else null
                        )
                    }
                }

                is UserEvents.SetPassword -> {
                    val isValid = passwordValidationUseCase(event.password)
                    _uiState.update {
                        it.copy(
                            password = event.password,
                            passwordError = if (!isValid) "Password must be at least 8 characters long" else null
                        )
                    }
                }

                is UserEvents.SetPhoneNo -> {
                    val isValid = phoneValidationUseCase(event.phoneNo)
                    _uiState.update {
                        it.copy(
                            phoneNo = event.phoneNo,
                            phoneNoError = if (!isValid) "Invalid phone number " else null
                        )
                    }
                }

                is UserEvents.OnRetypePassword -> {
                    _uiState.update {
                        it.copy(
                            retypedPassword = event.password,
                            retypedPasswordError = if (event.password != _uiState.value.password) "Passwords do not match" else null
                        )
                    }
                }

                is UserEvents.OnCountryChange -> {
                    _uiState.update { it.copy(country = event.country, showCountryChooser = false) }
                }

                UserEvents.ToggleCountryChooser -> {
                    _uiState.update { it.copy(showCountryChooser = !_uiState.value.showCountryChooser) }
                }

                UserEvents.TogglePasswordVisibility -> {
                    _uiState.update { it.copy(isPasswordVisible = !_uiState.value.isPasswordVisible) }
                }

                UserEvents.ToggleReenteredPasswordVisibility -> {
                    _uiState.update { it.copy(isReenteredPasswordVisible = !_uiState.value.isReenteredPasswordVisible) }
                }

                UserEvents.Submit -> {
                    if (_uiState.value.isSignUpEnabled && !_uiState.value.isLoading) {
                        signUpUserUseCase.signUpUser(
                            firstName = _uiState.value.firstName,
                            lastName = _uiState.value.lastName,
                            email = _uiState.value.email,
                            password = _uiState.value.password,
                            phoneNumber = _uiState.value.country.dialCode + _uiState.value.phoneNo
                        ).collect { state ->
                            var isHandled = false
                            if (state is ApiStates.Failure) {
                                val errorMsg = state.error.message
                                if (errorMsg.contains(
                                        "email",
                                        ignoreCase = true
                                    ) && (errorMsg.contains(
                                        "registered",
                                        ignoreCase = true
                                    ) || errorMsg.contains(
                                        "exist",
                                        ignoreCase = true
                                    ) || errorMsg.contains("taken", ignoreCase = true))
                                ) {
                                    _uiState.update {
                                        it.copy(
                                            emailError = "Email is already registered",
                                            isLoading = false
                                        )
                                    }
                                    isHandled = true
                                } else if ((errorMsg.contains(
                                        "phone",
                                        ignoreCase = true
                                    ) || errorMsg.contains(
                                        "mobile",
                                        ignoreCase = true
                                    )) && (errorMsg.contains(
                                        "registered",
                                        ignoreCase = true
                                    ) || errorMsg.contains(
                                        "exist",
                                        ignoreCase = true
                                    ) || errorMsg.contains("taken", ignoreCase = true))
                                ) {
                                    _uiState.update {
                                        it.copy(
                                            phoneNoError = "Phone number is already registered",
                                            isLoading = false
                                        )
                                    }
                                    isHandled = true
                                }
                            }

                            if (isHandled) {
                                updateSignUpState()
                            } else {
                                _uiState.update { it.copy(isLoading = state is ApiStates.Loading) }
                                _networkState.emit(state)
                            }
                        }

                    }
                }
            }
        }
        updateSignUpState()
    }

    private fun updateSignUpState() {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                isSignUpEnabled = state.emailError == null && state.passwordError == null && state.phoneNoError == null && state.firstNameError == null && state.lastNameError == null && state.retypedPasswordError == null && state.email.isNotEmpty() && state.password.isNotEmpty() && state.phoneNo.isNotEmpty() && state.firstName.isNotEmpty() && state.lastName.isNotEmpty() && state.retypedPassword == state.password
            )
        }
    }
}

