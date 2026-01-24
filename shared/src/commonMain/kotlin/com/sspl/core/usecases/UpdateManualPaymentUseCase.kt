package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ManualPaymentRequest
import com.sspl.core.models.Registration
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdateManualPaymentUseCase(private val repository: ConferenceRepository) {
    operator fun invoke(
        conferenceId: Long,
        registrationId: Long,
        request: ManualPaymentRequest
    ): Flow<ApiStates<Registration>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = repository.updatePayment(conferenceId, registrationId, request)
            if (response.status.value in 200..299) {
                emit(ApiStates.Success(response.body()))
            } else {
                emit(ApiStates.Failure(com.sspl.core.models.ApiError("Failed to update payment: ${response.status.description}")))
            }
        } catch (e: Exception) {
            emit(ApiStates.Failure(com.sspl.core.models.ApiError(e.message ?: "Unknown error occurred")))
        }
    }
}
