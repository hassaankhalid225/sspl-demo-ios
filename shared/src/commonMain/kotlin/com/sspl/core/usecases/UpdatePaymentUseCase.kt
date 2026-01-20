package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.UpdatePaymentRequest
import com.sspl.core.models.UpdatePaymentResponse
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UpdatePaymentUseCase(private val repository: ConferenceRepository) {
    operator fun invoke(conferenceId: Long, regId: Long, request: UpdatePaymentRequest): Flow<ApiStates<UpdatePaymentResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = repository.updatePayment(conferenceId, regId, request)
            if (response.status.isSuccess()) {
                emit(ApiStates.Success(response.body()))
            } else {
                emit(ApiStates.Failure(response.body()))
            }
        } catch (e: Exception) {
            emit(ApiStates.Failure(ApiError(message = e.message.orEmpty())))
        }
    }
}
