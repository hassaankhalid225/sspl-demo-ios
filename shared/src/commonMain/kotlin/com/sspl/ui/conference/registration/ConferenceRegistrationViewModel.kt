package com.sspl.ui.conference.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.session.UserSession

import com.sspl.core.models.ConferenceDetail
import com.sspl.core.models.RegisterUserRequest
import com.sspl.core.models.Registration
import com.sspl.core.models.Role
import com.sspl.core.models.InitiatePaymentRequest
import com.sspl.core.usecases.CheckRegistrationUseCase
import com.sspl.core.usecases.GetConferencesDetailsUseCase
import com.sspl.core.usecases.RegisterUserUseCase
import com.sspl.core.usecases.InitiatePaymentUseCase
import com.sspl.core.usecases.GetConferenceRolesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConferenceRegistrationViewModel(
    private val getConferenceDetailsUseCase: GetConferencesDetailsUseCase,
    private val checkRegistrationUseCase: CheckRegistrationUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val getConferenceRolesUseCase: GetConferenceRolesUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Loading)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun loadData(conferenceId: Long) {
        viewModelScope.launch {
            // First fetch details
            getConferenceDetailsUseCase.getConferenceById(conferenceId).collectLatest { detailsState ->
                when (detailsState) {
                    is ApiStates.Success -> {
                        detailsState.data?.let { details ->
                            // Now fetch roles and check registration
                            fetchRolesAndCheckRegistration(conferenceId, details)
                        } ?: run {
                            _uiState.value = RegistrationUiState.Error("Failed to load details: Data is null")
                        }
                    }
                    is ApiStates.Failure -> {
                        _uiState.value = RegistrationUiState.Error(detailsState.error.message ?: "Failed to load details")
                    }
                    else -> {
                        _uiState.value = RegistrationUiState.Loading
                    }
                }
            }
        }
    }

    private suspend fun fetchRolesAndCheckRegistration(conferenceId: Long, details: ConferenceDetail) {
        var roles: List<Role> = emptyList()
        
        getConferenceRolesUseCase(conferenceId).collectLatest { rolesState ->
             if (rolesState is ApiStates.Success) {
                 roles = rolesState.data ?: emptyList()
             }
        }

        checkRegistrationUseCase(conferenceId).collectLatest { state ->
             when(state) {
                 is ApiStates.Success -> {
                     state.data?.let { data ->
                         _uiState.value = RegistrationUiState.Content(
                             details = details,
                             registration = data.registration,
                             isRegistered = data.isRegistered,
                             roles = roles
                         )
                     } ?: run {
                         _uiState.value = RegistrationUiState.Error("Failed to check registration: Data is null")
                     }
                 }
                 is ApiStates.Failure -> {
                     _uiState.value = RegistrationUiState.Error(state.error.message ?: "Failed to check registration")
                 }
                 else -> {}
             }
        }
    }

    fun register(roleId: Long) {
        val currentState = _uiState.value
        if (currentState !is RegistrationUiState.Content) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)
            registerUserUseCase(currentState.details.id, RegisterUserRequest(roleId)).collectLatest { state ->
                when(state) {
                    is ApiStates.Success -> {
                        state.data?.let { data ->
                            _uiState.value = currentState.copy(isLoading = false, registration = data.registration, isRegistered = true, error = null)
                        } ?: run {
                            _uiState.value = currentState.copy(isLoading = false, error = "Registration failed: Data is null")
                        }
                    }
                    is ApiStates.Failure -> {
                        _uiState.value = currentState.copy(isLoading = false, error = state.error.message)
                    }
                    else -> {}
                }
            }
        }
    }

    fun initiatePayment() {
        val currentState = _uiState.value
        if (currentState !is RegistrationUiState.Content) return
        val registration = currentState.registration ?: return
        val regId = registration.id
        
        viewModelScope.launch {
            val user = userSession.getUser()
            if (user == null) {
                _uiState.value = currentState.copy(error = "User session not found")
                return@launch
            }
            
            val userId = user.id
            val email = user.account?.email ?: "noemail@example.com"
            val phone = user.account?.phone
            val name = "${user.firstName} ${user.lastName}"

            // Ensure we have a valid registration ID
            val request = InitiatePaymentRequest(
                itemType = "conference_registration",
                conferenceRegistrationId = regId,
                amount = registration.feeAmount,
                item = "Conference Registration - ${currentState.details.title}",
                email = email,
                name = name,
                phone = phone,
                userId = userId
            )
            
            _uiState.value = currentState.copy(isLoading = true)
            initiatePaymentUseCase(request).collectLatest { state ->
                when(state) {
                    is ApiStates.Success -> {
                         val paymentUrl = state.data?.bestPaymentUrl
                         if (paymentUrl != null) {
                             _uiState.value = currentState.copy(isLoading = false, paymentUrl = paymentUrl)
                         } else {
                             _uiState.value = currentState.copy(isLoading = false, error = "Payment URL not received")
                         }
                    }
                     is ApiStates.Failure -> {
                         _uiState.value = currentState.copy(isLoading = false, error = state.error.message)
                    }
                    else -> {}
                }
            }
        }
    }

    
    fun onPaymentUrlOpened() {
        val currentState = _uiState.value
        if (currentState is RegistrationUiState.Content) {
            _uiState.value = currentState.copy(paymentUrl = null) // Reset to avoid re-opening
        }
    }

    fun clearError() {
        val currentState = _uiState.value
        if (currentState is RegistrationUiState.Content) {
            _uiState.value = currentState.copy(error = null)
        }
    }
}

sealed class RegistrationUiState {
    data object Loading : RegistrationUiState()
    data class Error(val message: String) : RegistrationUiState()
    data class Content(
        val details: ConferenceDetail,
        val registration: Registration?,
        val isRegistered: Boolean,
        val roles: List<Role> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val paymentUrl: String? = null
    ) : RegistrationUiState()
}
