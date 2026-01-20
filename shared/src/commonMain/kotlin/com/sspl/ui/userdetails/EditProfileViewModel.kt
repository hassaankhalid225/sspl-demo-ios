package com.sspl.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.User
import com.sspl.core.usecases.PhoneNumberValidationUseCase
import com.sspl.core.usecases.PostUserDetailsUseCase
import com.sspl.core.usecases.UpdateUserBasicInfoUseCase
import com.sspl.core.usecases.ValidateUserDesignationUseCase
import com.sspl.core.usecases.ValidateUserInstitutionUseCase
import com.sspl.core.usecases.ValidateUserNameUseCase
import com.sspl.core.usecases.ValidateUserPmdcNoUseCase
import com.sspl.core.usecases.ValidateUserRegistrationNumberUseCase
import com.sspl.session.UserSession
import com.sspl.ui.components.countrypicker.Country
import com.sspl.ui.components.countrypicker.Pakistan
import com.sspl.ui.kyc.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel (
    private val userSession: UserSession,
    private val updateUserBasicInfoUseCase: UpdateUserBasicInfoUseCase,
    private val nameValidationUseCase: ValidateUserNameUseCase,
    private val phoneValidationUseCase: PhoneNumberValidationUseCase,
    private val validateUserDesignationUseCase: ValidateUserDesignationUseCase,
    private val validateUserInstitutionUseCase: ValidateUserInstitutionUseCase,
    private val validateUserRegistrationNumberUseCase: ValidateUserRegistrationNumberUseCase,
    private val validateUserPmDcNumberUseCase: ValidateUserPmdcNoUseCase,
    private val postUserDetailsUseCase: PostUserDetailsUseCase
):ViewModel() {

    private val _basicProfileUiState = MutableStateFlow(BasicProfileUiState())
    val basicProfileUiState = _basicProfileUiState.asStateFlow()

    private val _professionalProfileUiState = MutableStateFlow(ProfessionalProfileUiState())
    val professionalProfileUiState = _professionalProfileUiState.asStateFlow()


    private val _isUserProfessionalInfoUpdated: MutableSharedFlow<ApiStates<User>> =
        MutableSharedFlow()
    val isUserProfessionalInfoUpdated = _isUserProfessionalInfoUpdated.asSharedFlow()

    private val _isUserBasicInfoUpdated: MutableSharedFlow<ApiStates<User>> =
        MutableSharedFlow()
    val isUserBasicInfoUpdated = _isUserBasicInfoUpdated.asSharedFlow()

    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    init {
        viewModelScope.launch {
            println("Called ProfileViewModel")
            _user.value = userSession.getUser()
            _user.value?.let { mUser ->
                 _basicProfileUiState.update {
                    it.copy(
                        firstName = mUser.firstName.orEmpty(),
                        lastName = mUser.lastName.orEmpty(), isUpdateEnabled = true,
                        phone = mUser.account?.phone.orEmpty()

                    )
                }
            }
        }
    }

    fun onBasicProfileEvent(event: BasicProfileEvents) {
        when (event) {
            is BasicProfileEvents.OnCountryChange -> {
                _basicProfileUiState.value =
                    _basicProfileUiState.value.copy(
                        country = event.country,
                        showCountryChooser = false
                    )
            }

            is BasicProfileEvents.ToggleCountryChooser -> {
                _basicProfileUiState.value =
                    _basicProfileUiState.value.copy(showCountryChooser = !_basicProfileUiState.value.showCountryChooser)
            }


            is BasicProfileEvents.UpdatePhone -> {
                val isValid = phoneValidationUseCase(event.phone)
                _basicProfileUiState.update {
                    it.copy(
                        phone = event.phone,
                        phoneNoError = if (!isValid) "Invalid phone number " else null
                    )
                }
            }

            is BasicProfileEvents.OnSubmit -> {
                println("is submitted")
                updateUserBasicInfo()
                println("is submitted after")
            }

            is BasicProfileEvents.UpdateFirstName -> {
                val validationResult = nameValidationUseCase(event.name)
                _basicProfileUiState.update {
                    it.copy(
                        firstName = event.name,
                        firstNameError = if (validationResult is ValidationResult.Error) validationResult.message else null
                    )
                }
            }

            is BasicProfileEvents.UpdateLastName -> {
                val validationResult = nameValidationUseCase(event.name)
                _basicProfileUiState.update {
                    it.copy(
                        lastName = event.name,
                        lastNameError = if (validationResult is ValidationResult.Error) validationResult.message else null
                    )
                }
            }

        }
        updateBasicProfileUiState()
    }

    private fun updateUserBasicInfo() {
        println("🔵 updateUserBasicInfo() CALLED")
        println("🔹 Current state: ${_basicProfileUiState.value}")

        if (_basicProfileUiState.value.isUpdateEnabled && !_basicProfileUiState.value.isLoading) {
            println("✅ Conditions Met: Calling UseCase")
            updateUserBasicInfoUseCase(
                firstName = _basicProfileUiState.value.firstName,
                lastName = _basicProfileUiState.value.lastName,
                phone = _basicProfileUiState.value.phone
            ).onEach { state ->
                println("🟡 UseCase emitted: $state")

                _isUserBasicInfoUpdated.emit(state)

                _basicProfileUiState.update {
                    it.copy(isLoading = state is ApiStates.Loading)
                }

                if (state is ApiStates.Success) {
                    state.data?.let { newProfile ->
                        val newUser = _user.value?.copy(
                            account = _user.value?.account?.copy(phone = newProfile.account?.phone.orEmpty()),
                            firstName = newProfile.firstName.orEmpty(),
                            lastName = newProfile.lastName.orEmpty()
                        )
                        newUser?.let { userSession.setUser(it) }
                        _user.update { newUser }
                    }
                }
            }.launchIn(viewModelScope)
        } else {
            println("🚫 Conditions NOT met: Skipping UseCase")
        }
    }

    private fun updateBasicProfileUiState() {
        val state = _basicProfileUiState.value
        _basicProfileUiState.update {
            it.copy(
                isUpdateEnabled = state.phoneNoError == null && state.firstNameError == null && state.lastNameError == null && state.phone.isNotEmpty() && state.firstName.isNotEmpty() && state.lastName.isNotEmpty()
            )
        }
    }

    fun onProfessionalEvent(event: ProfessionalProfileEvents) {
        when (event) {
            is ProfessionalProfileEvents.OnDesignationTextChange -> {
                val result = validateUserDesignationUseCase(event.designationText)
                _professionalProfileUiState.update {
                    it.copy(
                        designation = event.designationText,
                        designationError = if (result is ValidationResult.Error) "Designation can't be empty" else null
                    ).apply { updateSaveEnabledState() }
                }
            }

            is ProfessionalProfileEvents.OnInstitutionTextChange -> {
                val result = validateUserInstitutionUseCase(event.institutionText)
                _professionalProfileUiState.update {
                    it.copy(
                        institution = event.institutionText,
                        institutionError = if (result is ValidationResult.Error) "Institution can't be empty" else null
                    ).apply { updateSaveEnabledState() }
                }
            }

            is ProfessionalProfileEvents.OnPmDcNumberTextChange -> {
                val result = validateUserPmDcNumberUseCase(event.pmDcNumberText)
                _professionalProfileUiState.update {
                    it.copy(
                        pmDcNumber = event.pmDcNumberText,
                        pmDcNumberError = if (result is ValidationResult.Error) "PMDC Number can't be empty" else null
                    ).apply { updateSaveEnabledState() }
                }
            }

            is ProfessionalProfileEvents.OnRegistrationNumberTextChange -> {
                val result = validateUserRegistrationNumberUseCase(event.registrationNumberText)
                _professionalProfileUiState.update {
                    it.copy(
                        registrationNumber = event.registrationNumberText,
                        registrationNumberError = if (result is ValidationResult.Error) "Registration number can't be empty" else null
                    ).apply { updateSaveEnabledState() }
                }
            }

            ProfessionalProfileEvents.OnSave -> {
                updateUserProfessionalInfo()
            }
        }
    }

    private fun updateSaveEnabledState() {
        _professionalProfileUiState.update { currentState ->
            currentState.copy(
                isUpdateEnabled = listOf(
                    currentState.institution,
                    currentState.registrationNumber
                ).all { field -> field.isNotEmpty() } && listOf(
                    currentState.institutionError,
                    currentState.registrationNumberError,
                ).all { error -> error == null })
        }
    }

    private fun updateUserProfessionalInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            println("Fucking Launch")
            if (_professionalProfileUiState.value.isUpdateEnabled && !_professionalProfileUiState.value.isLoading) {

                postUserDetailsUseCase.invoke(
                    pmdcNo = _professionalProfileUiState.value.pmDcNumber.orEmpty(),
                    registrationNo = _professionalProfileUiState.value.registrationNumber,
                    instituteName = _professionalProfileUiState.value.institution,
                    designation = _professionalProfileUiState.value.designation
                ).collect { state ->

                    _isUserProfessionalInfoUpdated.emit(state)
                    _professionalProfileUiState.update {
                        it.copy(isLoading = state is ApiStates.Loading)
                    }

                }
            }
        }
    }

     fun updateProfessionalProfileUiState(data: User?) {
         data?.let {
             _professionalProfileUiState.update {
                 it.copy(
                     institution = data.profile?.institute.orEmpty(),
                     pmDcNumber = data.profile?.pmdcNumber.orEmpty(),
                     registrationNumber = data.profile?.orgNumber.orEmpty(),
                     designation = data.profile?.title.orEmpty(),
                     isUpdateEnabled = true
                 )
             }
         }
    }


}
data class BasicProfileUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val isUpdateEnabled: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val phoneNoError: String? = null,
    val country: Country = Pakistan,
    val showCountryChooser: Boolean = false,
)

data class ProfessionalProfileUiState(
    val institution: String = "",
    val pmDcNumber: String? = "",
    val registrationNumber: String = "",
    val designation: String = "",
    val institutionError: String? = null,
    val pmDcNumberError: String? = null,
    val registrationNumberError: String? = null,
    val designationError: String? = null,
    val isLoading: Boolean = false,
    val isUpdateEnabled: Boolean = false,
)


sealed interface BasicProfileEvents {
    data class UpdateFirstName(val name: String) : BasicProfileEvents
    data class UpdateLastName(val name: String) : BasicProfileEvents
    data class OnCountryChange(val country: Country) : BasicProfileEvents
    data class UpdatePhone(val phone: String) : BasicProfileEvents
    data object ToggleCountryChooser : BasicProfileEvents
    data object OnSubmit : BasicProfileEvents
}


sealed interface ProfessionalProfileEvents {
    data object OnSave : ProfessionalProfileEvents
    data class OnInstitutionTextChange(val institutionText: String) : ProfessionalProfileEvents
    data class OnPmDcNumberTextChange(val pmDcNumberText: String) : ProfessionalProfileEvents
    data class OnRegistrationNumberTextChange(val registrationNumberText: String) :
        ProfessionalProfileEvents

    data class OnDesignationTextChange(val designationText: String) : ProfessionalProfileEvents
}