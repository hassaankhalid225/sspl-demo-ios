package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.Role
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetConferenceRolesUseCase(private val repository: ConferenceRepository) {
    suspend operator fun invoke(conferenceId: Long): Flow<ApiStates<List<Role>>> = flow {
        emit(ApiStates.Loading)
        try {
            val response: HttpResponse = repository.getConferenceRoles(conferenceId)
            if (response.status == HttpStatusCode.OK) {
                val data = response.body<List<Role>>()
                emit(ApiStates.Success(data))
            } else {
                emit(ApiStates.Failure(ApiError("Failed to fetch roles: ${response.status}")))
            }
        } catch (e: Exception) {
            emit(ApiStates.Failure(ApiError(e.message ?: "Unknown error")))
        }
    }
}
