package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.InitResetPassword
import com.sspl.core.models.PasswordToken
import com.sspl.core.repositories.AuthRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class InitResetPasswordUseCase(
    private val authRepository: AuthRepository,
) {

    operator fun invoke(
        email: String,
    ): Flow<ApiStates<PasswordToken>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = authRepository.initResetPassword(body = InitResetPassword(email = email))
            if (response.status.isSuccess()) {
                 val result = response.body<PasswordToken>()
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