package com.sspl.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.User

import com.sspl.core.usecases.PostUserDetailsUseCase
import com.sspl.core.usecases.ValidateUserCityUseCase
import com.sspl.core.usecases.ValidateUserCountryUseCase
import com.sspl.core.usecases.ValidateUserDesignationUseCase
import com.sspl.core.usecases.ValidateUserInstitutionUseCase
import com.sspl.core.usecases.ValidateUserPmdcNoUseCase
import com.sspl.core.usecases.ValidateUserRegistrationNumberUseCase
import com.sspl.ui.kyc.ValidationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserDetailViewModel(
    private val validateUserCityUseCase: ValidateUserCityUseCase,
    private val validateUserCountryUseCase: ValidateUserCountryUseCase,
    private val validateUserDesignationUseCase: ValidateUserDesignationUseCase,
    private val validateUserInstitutionUseCase: ValidateUserInstitutionUseCase,
    private val validateUserRegistrationNumberUseCase: ValidateUserRegistrationNumberUseCase,
    private val validateUserPmDcNumberUseCase: ValidateUserPmdcNoUseCase,
    private val postUserDetailsUseCase: PostUserDetailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private val _networkState = MutableSharedFlow<ApiStates<User>>()
    val networkState: SharedFlow<ApiStates<User>> = _networkState.asSharedFlow()

    fun onEvent(event: UserDetailsUiEvents) {
        viewModelScope.launch {
            when (event) {
                is UserDetailsUiEvents.OnCityTextChange -> {
                    val result = validateUserCityUseCase(event.cityText)
                    _uiState.update {
                        it.copy(
                            city = event.cityText,
                            cityError = if (result is ValidationResult.Error) "City can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                is UserDetailsUiEvents.ToggleCountryPicker -> {
                    _uiState.update {
                        it.copy(
                            showCountryPicker = !it.showCountryPicker
                        )
                    }
                }

                is UserDetailsUiEvents.OnCountryChange -> {
                    val country = event.country.ifBlank {
                        _uiState.value.country
                    }
                    val result = validateUserCountryUseCase(country)
                    _uiState.update {
                        it.copy(
                            country = country, showCountryPicker = false,
                            countryError = if (result is ValidationResult.Error) "Country can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                is UserDetailsUiEvents.OnDesignationTextChange -> {
                    val result = validateUserDesignationUseCase(event.designationText)
                    _uiState.update {
                        it.copy(
                            designation = event.designationText,
                            designationError = if (result is ValidationResult.Error) "Designation can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                is UserDetailsUiEvents.OnInstitutionTextChange -> {
                    val result = validateUserInstitutionUseCase(event.institutionText)
                    _uiState.update {
                        it.copy(
                            institution = event.institutionText,
                            institutionError = if (result is ValidationResult.Error) "Institution can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                is UserDetailsUiEvents.OnPmDcNumberTextChange -> {
                    val result = validateUserPmDcNumberUseCase(event.pmDcNumberText)
                    _uiState.update {
                        it.copy(
                            pmDcNumber = event.pmDcNumberText,
                            pmDcNumberError = if (result is ValidationResult.Error) "PMDC Number can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                is UserDetailsUiEvents.OnRegistrationNumberTextChange -> {
                    val result = validateUserRegistrationNumberUseCase(event.registrationNumberText)
                    _uiState.update {
                        it.copy(
                            registrationNumber = event.registrationNumberText,
                            registrationNumberError = if (result is ValidationResult.Error) "Registration number can't be empty" else null
                        ).apply { updateSaveEnabledState() }
                    }
                }

                UserDetailsUiEvents.OnSave -> {
                    if (_uiState.value.isSaveEnabled && !_uiState.value.isLoading) {
                        postUserDetailsUseCase(
                            pmdcNo = _uiState.value.pmDcNumber.orEmpty(),
                            instituteName = _uiState.value.institution,
                            country = _uiState.value.country,
                            designation = _uiState.value.designation,
                            registrationNo = _uiState.value.registrationNumber
                        ).collect { state ->
                            _uiState.update { it.copy(isLoading = state is ApiStates.Loading) }
                            _networkState.emit(state)
                        }
                    }
                }
            }
        }
    }

    // Helper function to update the save button enabled state based on field validation
    private fun updateSaveEnabledState() {
        _uiState.update { currentState ->
            currentState.copy(
                isSaveEnabled = listOf(
                    currentState.institution,
//                currentState.city,
                    currentState.country,
                    currentState.registrationNumber
                ).all { field -> field.isNotEmpty() } && listOf(
                    currentState.institutionError,
//                currentState.cityError,
                    currentState.countryError,
                    currentState.registrationNumberError,
                ).all { error -> error == null })
        }
    }
}


data class UserDetailsUiState(
    val city: String = "",
    val country: String = "",
    val institution: String = "",
    val pmDcNumber: String? = "",
    val registrationNumber: String = "",
    val designation: String = "",
    val institutionError: String? = null,
    val cityError: String? = null,
    val showCountryPicker: Boolean = false,
    val countryError: String? = null,
    val pmDcNumberError: String? = null,
    val registrationNumberError: String? = null,
    val designationError: String? = null,
    val isSaveEnabled: Boolean = false,
    val isLoading: Boolean = false
)

sealed interface UserDetailsUiEvents {
    data object OnSave : UserDetailsUiEvents
    data object ToggleCountryPicker : UserDetailsUiEvents
    data class OnInstitutionTextChange(val institutionText: String) : UserDetailsUiEvents
    data class OnCityTextChange(val cityText: String) : UserDetailsUiEvents
    data class OnCountryChange(val country: String) : UserDetailsUiEvents
    data class OnPmDcNumberTextChange(val pmDcNumberText: String) : UserDetailsUiEvents
    data class OnRegistrationNumberTextChange(val registrationNumberText: String) :
        UserDetailsUiEvents

    data class OnDesignationTextChange(val designationText: String) : UserDetailsUiEvents
}