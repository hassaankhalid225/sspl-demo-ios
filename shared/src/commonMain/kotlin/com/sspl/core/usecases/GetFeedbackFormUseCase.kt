package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.FeedbackForm
import com.sspl.core.models.InitResetPassword
import com.sspl.core.models.PasswordToken
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 12/02/2025.
 * se.muhammadimran@gmail.com
 */
class GetFeedbackFormUseCase(private val conferenceRepository: ConferenceRepository) {
    operator fun invoke(conferenceId: Long): Flow<ApiStates<FeedbackForm>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = conferenceRepository.getFeedbackForm(conferenceId = conferenceId)
            if (response.status.isSuccess()) {
                print("the response is  ${response.body<String>()}")
                val result = response.body<FeedbackForm?>()
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