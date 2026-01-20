package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.Conference
import com.sspl.core.models.ConferenceType
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetConferencesUseCase(private val conferenceRepository: ConferenceRepository) {


    fun getAllConferences(type: ConferenceType = ConferenceType.ALL): Flow<ApiStates<List<Conference>>> =
        flow {
            emit(ApiStates.Loading)
            try {
                val strType = if (type == ConferenceType.ALL) null else type.name.lowercase()
                println(">>>Usecase Conference Request:strType =$strType")
                val response = conferenceRepository.getAllConferences(type = strType)

                if (response.status.isSuccess()) {
                    val result = response.body<List<Conference>>()
                    emit(ApiStates.Success(result))
                } else {
                    val errorResponse = response.body<ApiError>()
                    emit(ApiStates.Failure(error = errorResponse))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                emit(ApiStates.Failure(ApiError(message = e.message.orEmpty())))
            }
        }
}
