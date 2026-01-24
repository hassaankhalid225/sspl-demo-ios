package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.InitiatePaymentRequest
import com.sspl.core.models.InitiatePaymentResponse
import com.sspl.core.repositories.PaymentRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import com.sspl.core.models.ApiError

class InitiatePaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(request: InitiatePaymentRequest): Flow<ApiStates<InitiatePaymentResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val response: HttpResponse = repository.initiatePayment(request)
            if (response.status == HttpStatusCode.OK) {
                val data = response.body<InitiatePaymentResponse>()
                emit(ApiStates.Success(data))
            } else {
                val errorBody = response.body<String>()
                try {
                    val apiError = kotlinx.serialization.json.Json.decodeFromString<ApiError>(errorBody)
                    emit(ApiStates.Failure(apiError))
                } catch (e: Exception) {
                    emit(ApiStates.Failure(ApiError("Failed to initiate payment: ${response.status}\n$errorBody")))
                }
            }
        } catch (e: Exception) {
            emit(ApiStates.Failure(ApiError(e.message ?: "Unknown error")))
        }
    }
}
