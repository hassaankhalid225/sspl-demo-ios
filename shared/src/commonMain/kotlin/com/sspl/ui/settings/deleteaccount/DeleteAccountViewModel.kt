package com.sspl.ui.settings.deleteaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.CommonResponse
import com.sspl.core.models.User
import com.sspl.core.usecases.DeleteAccountUseCase
import com.sspl.session.UserSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
class DeleteAccountViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val userSession: UserSession
) : ViewModel() {
    private val _uiState = MutableStateFlow<UIState>(UIState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                _uiState.value.copy(
                    isLoading = false,
                    user = userSession.getUser()
                )
            }
        }
    }

    /**
     * Delete user's account permanently and clear data locally by Logging out
     */
    fun deleteAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userSession.getUser()
            user?.account?.email?.let {
                deleteAccountUseCase.invoke(it).collect { data ->
                    when (data) {
                        is ApiStates.Loading -> _uiState.update {
                            _uiState.value.copy(
                                deleteAcRes = null,
                                isLoading = true
                            )
                        }

                        is ApiStates.Failure -> _uiState.update {
                            _uiState.value.copy(
                                deleteAcRes = null,
                                error = data.error,
                                isLoading = false
                            )
                        }

                        ApiStates.Idle -> {} // Do nothing

                        is ApiStates.Success -> _uiState.update {
                            _uiState.value.copy(
                                deleteAcRes = data.data,
                                error = null,
                                isLoading = false
                            )
                        }
                    }
                }
            }

        }
    }
}

data class UIState(
    val isLoading: Boolean = true,
    val error: ApiError? = null,
    val user: User? = null,
    val deleteAcRes: CommonResponse? = null
)

sealed interface UIStateEvent {
    data object Loading : UIStateEvent
    data class UserLoaded(val user: User) : UIStateEvent
    data class AccountDeleted(val response: CommonResponse) : UIStateEvent
    data class OnError(val error: ApiError) : UIStateEvent
}