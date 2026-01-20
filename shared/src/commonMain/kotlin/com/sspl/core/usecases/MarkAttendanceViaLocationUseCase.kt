package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.AttendanceResponse
import com.sspl.core.models.ConferenceDetail
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class MarkAttendanceViaLocationUseCase(private val conferenceRepository: ConferenceRepository) {
    operator fun invoke(conferenceId: Long, sessionId: Long): Flow<ApiStates<AttendanceResponse>> =
        flow {
            emit(ApiStates.Loading)
            try {
                println(conferenceId.toString() + "is the id ")
                val response =
                    conferenceRepository.markAttendanceViaLocation(conferenceId, sessionId)

                if (response.status.isSuccess()) {
                    val result = response.body<AttendanceResponse>()
                    println("The result is $result")
                    emit(ApiStates.Success(result))
                } else {
                    println("new result is " + response.body())
                    val errorResponse = response.body<ApiError>()
                    println("The result is $errorResponse")

                    emit(ApiStates.Failure(error = errorResponse))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiStates.Failure(ApiError(message = e.message.orEmpty())))
            }
        }
}
