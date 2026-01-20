package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.ParticipantResponse
import com.sspl.core.repositories.ScenarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Use case for auto-joining a session using authentication token
 * No name/email required - extracted from token by backend
 */
class AutoJoinSessionUseCase(
    private val scenarioRepository: ScenarioRepository
) {
    operator fun invoke(joinCode: String): Flow<ApiStates<ParticipantResponse>> = flow {
        emit(ApiStates.Loading)
        
        scenarioRepository.autoJoinSession(joinCode)
            .catch { exception ->
                exception.printStackTrace()
                emit(ApiStates.Failure(ApiError(message = exception.message.orEmpty())))
            }
            .collect { response ->
                emit(ApiStates.Success(response))
            }
    }
}
