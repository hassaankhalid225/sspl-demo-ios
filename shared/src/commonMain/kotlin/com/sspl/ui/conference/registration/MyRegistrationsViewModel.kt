package com.sspl.ui.conference.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.Registration
import com.sspl.core.usecases.GetUserRegistrationsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MyRegistrationsViewModel(
    private val getUserRegistrationsUseCase: GetUserRegistrationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MyRegistrationsUiState>(MyRegistrationsUiState.Loading)
    val uiState: StateFlow<MyRegistrationsUiState> = _uiState.asStateFlow()

    fun loadRegistrations(conferenceId: Long) {
        viewModelScope.launch {
            _uiState.value = MyRegistrationsUiState.Loading
            getUserRegistrationsUseCase(conferenceId).collectLatest { state ->
                when (state) {
                    is ApiStates.Success -> {
                        val records = state.data?.records ?: emptyList()
                        _uiState.value = MyRegistrationsUiState.Success(records)
                    }
                    is ApiStates.Failure -> {
                        _uiState.value = MyRegistrationsUiState.Error(state.error.message ?: "Failed to load registrations")
                    }
                    is ApiStates.Loading -> {
                         _uiState.value = MyRegistrationsUiState.Loading
                    }
                    else -> {} // Handle Idle or other states
                }
            }
        }
    }
}

sealed class MyRegistrationsUiState {
    data object Loading : MyRegistrationsUiState()
    data class Success(val registrations: List<Registration>) : MyRegistrationsUiState()
    data class Error(val message: String) : MyRegistrationsUiState()
}
