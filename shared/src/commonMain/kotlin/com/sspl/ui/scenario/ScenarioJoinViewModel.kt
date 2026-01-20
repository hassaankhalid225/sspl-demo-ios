package com.sspl.ui.scenario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.ParticipantResponse
import com.sspl.core.usecases.JoinSessionUseCase
import com.sspl.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScenarioJoinViewModel(
    private val joinSessionUseCase: JoinSessionUseCase,
    private val userSession: UserSession
) : ViewModel() {

    private val _joinState = MutableStateFlow<ApiStates<ParticipantResponse>>(ApiStates.Idle)
    val joinState: StateFlow<ApiStates<ParticipantResponse>> = _joinState.asStateFlow()

    fun joinSession(joinCode: String, name: String, email: String) {
        viewModelScope.launch {
            val deviceToken = userSession.deviceToken
            val devicePlatform = "android" // Hardcoded for androidApp, ideally this would be platform-specific

            joinSessionUseCase(
                joinCode = joinCode,
                name = name,
                email = email,
                deviceToken = deviceToken,
                devicePlatform = devicePlatform
            ).collect {
                _joinState.value = it
            }
        }
    }
}
