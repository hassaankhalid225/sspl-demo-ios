package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.CommonResponse
import com.sspl.core.repositories.AuthRepository
import com.sspl.core.requestbodies.DeleteAccountBody
import com.sspl.session.UserSession
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
class DeleteAccountUseCase(
    private val authRepository: AuthRepository,
    private val userSession: UserSession
) {

    operator fun invoke(email: String) = flow<ApiStates<CommonResponse>> {
        emit(ApiStates.Loading)
        try {
            val response = authRepository.deleteAccount(DeleteAccountBody(email = email))
            if (response.status.isSuccess()) {
                val result = response.body<CommonResponse>()
                emit(ApiStates.Success(result))
                userSession.resetToken()
                userSession.resetUser()

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