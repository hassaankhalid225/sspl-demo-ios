package com.sspl.ui.userdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.User
import com.sspl.core.repositories.UserRepository
import com.sspl.core.usecases.GetUserDetailsUseCase
import com.sspl.core.usecases.PhoneNumberValidationUseCase
import com.sspl.core.usecases.PostUserDetailsUseCase
import com.sspl.core.usecases.UpdateUserBasicInfoUseCase
import com.sspl.core.usecases.ValidateUserCityUseCase
import com.sspl.core.usecases.ValidateUserCountryUseCase
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userSession: UserSession,
    private val getUserDetailsUseCase: GetUserDetailsUseCase,

    ) : ViewModel() {
    //throughtout the app single obj
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    var isGuestUser = MutableStateFlow<Boolean?>(null)
        private set

    //only for profile details
    private val _userDetails = MutableStateFlow<ApiStates<User?>>(ApiStates.Loading)
    val userDetails = _userDetails.asStateFlow()

    var onSignedOut = MutableStateFlow(false)
        private set

    init {
        viewModelScope.launch {
            println("Called ProfileViewModel")
            val user = userSession.getUser()
            _user.value = user
            isGuestUser.update { user?.account?.isGuestUser() ?: true }
        }
    }

    fun updateUser(user: User) {
        _user.update { user }
        _userDetails.update { ApiStates.Success(user) }
    }

    fun updateUserDetails(userDetails: User) {
        _userDetails.update { ApiStates.Success(userDetails) }
        _user.update { userDetails }
    }

    fun getUserDetails() {
        viewModelScope.launch {
            getUserDetailsUseCase.invoke().collect { state ->
                _userDetails.value = state
                if (state is ApiStates.Success) {
                    state.data?.let { data ->
                        _user.update { data }
                    }
                }
            }
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

