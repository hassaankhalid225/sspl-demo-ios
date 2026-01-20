package com.sspl.ui.kyc

import com.sspl.ui.components.countrypicker.Country
import com.sspl.ui.components.countrypicker.Pakistan


data class UserUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String = "",
    val passwordError: String? = null,
    val emailError: String? = null,
    val retypedPasswordError: String? = null,
    val phoneNoError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val isSignUpEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val phoneNo: String = "",
    val retypedPassword: String = "",
    val country: Country = Pakistan,
    val showCountryChooser: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isReenteredPasswordVisible: Boolean = false,

    )

sealed interface UserEvents {
    data class SetFirstName(val firstName: String) : UserEvents
    data class SetLastName(val lastName: String) : UserEvents
    data class SetEmail(val email: String) : UserEvents
    data class SetPassword(val password: String) : UserEvents
    data class OnRetypePassword(val password: String) : UserEvents
    data class SetPhoneNo(val phoneNo: String) : UserEvents
    data class OnCountryChange(val country: Country) : UserEvents
    data object ToggleCountryChooser : UserEvents
    data object TogglePasswordVisibility : UserEvents
    data object ToggleReenteredPasswordVisibility : UserEvents
    data object Submit : UserEvents
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()
}
