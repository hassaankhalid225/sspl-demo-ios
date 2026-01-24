package com.sspl.core.usecases


import com.sspl.core.ApiStates
import com.sspl.core.models.Account
import com.sspl.core.models.ApiError
import com.sspl.core.models.User
import com.sspl.core.models.UserResponse
import com.sspl.core.repositories.AuthRepository
import com.sspl.session.UserSession
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SignUpUserUseCase(
    private val authRepository: AuthRepository, private val userSession: UserSession
) {

    fun signUpUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        phoneNumber: String
    ): Flow<ApiStates<UserResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val user = User(
                id = 0L,
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                account = Account(
                    email = email.trim(),
                    password = password.trim(),
                    phone = phoneNumber.trim()
                )
            )
            val response = authRepository.signUp(body = user)

            if (response.status.isSuccess()) {
                val result = response.body<UserResponse>()
                result.accessToken?.let { userSession.setToken(it) }
                result.user?.let { userSession.setUser(it) }

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

