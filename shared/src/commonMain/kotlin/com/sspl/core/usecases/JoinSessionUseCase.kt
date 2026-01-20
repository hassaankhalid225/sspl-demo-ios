package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.ParticipantResponse
import com.sspl.core.repositories.ScenarioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class JoinSessionUseCase(private val scenarioRepository: ScenarioRepository) {
    
    operator fun invoke(
        joinCode: String,
        name: String,
        email: String,
        deviceToken: String?,
        devicePlatform: String?
    ): Flow<ApiStates<ParticipantResponse>> = flow {
        emit(ApiStates.Loading)
        scenarioRepository.joinSession(
            joinCode = joinCode,
            name = name,
            email = email,
            deviceToken = deviceToken,
            devicePlatform = devicePlatform
        ).map { 
            ApiStates.Success(it) 
        }.catch { e ->
            emit(ApiStates.Failure(ApiError(message = e.message ?: "Unknown error occurred")))
        }.collect {
            emit(it)
        }
    }
}
