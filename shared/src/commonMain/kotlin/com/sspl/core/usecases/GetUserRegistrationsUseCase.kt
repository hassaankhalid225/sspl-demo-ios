package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.RegistrationListResponse
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import com.sspl.core.models.ApiError

class GetUserRegistrationsUseCase(private val repository: ConferenceRepository) {
    suspend operator fun invoke(
        conferenceId: Long,
        page: Int = 1,
        limit: Int = 20,
        paymentStatus: String? = null
    ): Flow<ApiStates<RegistrationListResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val response: HttpResponse = repository.getUserRegistrations(conferenceId, page, limit, paymentStatus)
            if (response.status == HttpStatusCode.OK) {
                val data = response.body<RegistrationListResponse>()
                emit(ApiStates.Success(data))
            } else {
                emit(ApiStates.Failure(ApiError("Failed to fetch registrations: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(ApiStates.Failure(ApiError(e.message ?: "Unknown error")))
        }
    }
}
