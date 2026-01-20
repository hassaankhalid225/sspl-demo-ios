package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.CommonResponse
import com.sspl.core.models.FeedbackForm
import com.sspl.core.models.Option
import com.sspl.core.models.Statement
import com.sspl.core.repositories.ConferenceRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


class PostFeedbackFormUseCase(private val conferenceRepository: ConferenceRepository) {
    operator fun invoke(selectedAnswers: List<Pair<Statement, Option>>,conferenceId: Long,sessionId: Long? = null): Flow<ApiStates<CommonResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val jsonBody = buildJsonObject {
                put("data", buildJsonArray {
                    selectedAnswers.forEach { (statement, option) ->
                        addJsonObject {
                            put("question_id", statement.id)
                            put("value", option.value)
                        }
                    }
                })
            }

            val response = sessionId?.let {
                conferenceRepository.postFeedbackFormSession(
                    conferenceId = conferenceId,
                    sessionId=it,
                    body = jsonBody
                )
            }?:conferenceRepository.postFeedbackForm(conferenceId, body = jsonBody)
            if (response.status.isSuccess()) {
                val responseBody = response.body<CommonResponse>()
                 emit(ApiStates.Success(responseBody))
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