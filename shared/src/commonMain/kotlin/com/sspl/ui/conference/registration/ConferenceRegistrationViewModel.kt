package com.sspl.ui.conference.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.ConferenceDetail
import com.sspl.core.models.RegisterUserRequest
import com.sspl.core.models.Registration
import com.sspl.core.models.UpdatePaymentRequest
import com.sspl.core.usecases.CheckRegistrationUseCase
import com.sspl.core.usecases.GetConferencesDetailsUseCase
import com.sspl.core.usecases.RegisterUserUseCase
import com.sspl.core.usecases.UpdatePaymentUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConferenceRegistrationViewModel(
    private val getConferenceDetailsUseCase: GetConferencesDetailsUseCase,
    private val checkRegistrationUseCase: CheckRegistrationUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val updatePaymentUseCase: UpdatePaymentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegistrationUiState>(RegistrationUiState.Loading)
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()

    fun loadData(conferenceId: Long) {
        viewModelScope.launch {
            getConferenceDetailsUseCase.getConferenceById(conferenceId).collectLatest { state ->
                when (state) {
                    is ApiStates.Success -> {
                        state.data?.let { details ->
                            checkRegistration(conferenceId, details)
                        } ?: run {
                            _uiState.value = RegistrationUiState.Error("Failed to load details: Data is null")
                        }
                    }
                    is ApiStates.Failure -> {
                        _uiState.value = RegistrationUiState.Error(state.error.message ?: "Failed to load details")
                    }
                    else -> {
                        _uiState.value = RegistrationUiState.Loading
                    }
                }
            }
        }
    }

    private suspend fun checkRegistration(conferenceId: Long, details: ConferenceDetail) {
         checkRegistrationUseCase(conferenceId).collectLatest { state ->
             when(state) {
                 is ApiStates.Success -> {
                     state.data?.let { data ->
                         _uiState.value = RegistrationUiState.Content(details, data.registration, data.isRegistered)
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
                            _uiState.value = currentState.copy(isLoading = false, registration = data.registration, isRegistered = true)
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

    fun updatePayment(mode: String, bankName: String? = null, txnId: String? = null) {
        val currentState = _uiState.value
        if (currentState !is RegistrationUiState.Content) return
        val regId = currentState.registration?.id ?: return

        val request = UpdatePaymentRequest(
            paymentStatus = "SUCCESS",
            paymentMode = mode,
            bankName = bankName,
            transactionId = txnId
        )

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true)
            updatePaymentUseCase(currentState.details.id, regId, request).collectLatest { state ->
                when(state) {
                     is ApiStates.Success -> {
                         state.data?.let { data ->
                             _uiState.value = currentState.copy(isLoading = false, registration = data.registration)
                         } ?: run {
                             _uiState.value = currentState.copy(isLoading = false, error = "Payment update failed: Data is null")
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
        val isLoading: Boolean = false,
        val error: String? = null
    ) : RegistrationUiState()
}
