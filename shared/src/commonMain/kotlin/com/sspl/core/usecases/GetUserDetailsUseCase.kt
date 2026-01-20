package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.User
import com.sspl.core.repositories.UserRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserDetailsUseCase(
    private val userRepository: UserRepository,
 ) {
    operator fun invoke(
    ): Flow<ApiStates<User>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = userRepository.getUserProfile()
            if (response.status.isSuccess()) {
                println("the result is $response")
                val result = response.body<User>()
                println("the result is $result")
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