package com.sspl.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.User
import com.sspl.core.usecases.GetUserDetailsUseCase
import com.sspl.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class ProfileViewModel(
    private val userSession: UserSession,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,
) : ViewModel() {
    
    val user = userSession.userFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    
    val isGuestUser = userSession.userFlow.map { user ->
        user?.account?.isGuestUser() ?: true
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _userDetails = MutableStateFlow<ApiStates<User?>>(ApiStates.Loading)
    val userDetails = _userDetails.asStateFlow()

    val profileImage = userSession.profileImageFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    var onSignedOut = MutableStateFlow(false)
        private set

    fun updateUser(user: User) {
        viewModelScope.launch {
            userSession.setUser(user)
            _userDetails.update { ApiStates.Success(user) }
        }
    }

    fun updateUserDetails(userDetails: User) {
        viewModelScope.launch {
            userSession.setUser(userDetails)
            _userDetails.update { ApiStates.Success(userDetails) }
        }
    }

    fun getUserDetails() {
        viewModelScope.launch {
            getUserDetailsUseCase.invoke().collect { state ->
                _userDetails.value = state
                if (state is ApiStates.Success) {
                    state.data?.let { data ->
                        userSession.setUser(data)
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun updateProfileImage(byteArray: ByteArray) {
        viewModelScope.launch {
            val base64 = Base64.encode(byteArray)
            userSession.setProfileImage(base64)
        }
    }

    fun onSignOut() {
        viewModelScope.launch {
            userSession.resetToken()
            userSession.resetUser()
            onSignedOut.update { true }
        }
    }
}
