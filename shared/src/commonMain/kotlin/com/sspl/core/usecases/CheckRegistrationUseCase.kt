package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.CheckRegistrationResponse
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CheckRegistrationUseCase(private val conferenceRepository: ConferenceRepository) {
    operator fun invoke(conferenceId: Long): Flow<ApiStates<CheckRegistrationResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = conferenceRepository.checkRegistration(conferenceId)
            if (response.status.isSuccess()) {
                val result = response.body<CheckRegistrationResponse>()
                emit(ApiStates.Success(result))
            } else {
                val errorBody = response.body<String>()
                try {
                    // Try to parse as JSON
                    val errorResponse = kotlinx.serialization.json.Json.decodeFromString<ApiError>(errorBody)
                    emit(ApiStates.Failure(errorResponse))
                } catch (e: Exception) {
                    // Fallback for non-JSON errors (HTML, plain text, etc)
                    emit(ApiStates.Failure(ApiError(message = "Error: ${response.status}", error = errorBody)))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ApiStates.Failure(ApiError(message = e.message.orEmpty())))
        }
    }
}
