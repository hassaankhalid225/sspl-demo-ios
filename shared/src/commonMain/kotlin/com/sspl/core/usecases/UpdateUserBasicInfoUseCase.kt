package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.Account
import com.sspl.core.models.ApiError
import com.sspl.core.models.Profile
import com.sspl.core.models.User
import com.sspl.core.repositories.UserRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class UpdateUserBasicInfoUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(
        firstName: String,
        lastName: String,
        phone: String
    ): Flow<ApiStates<User>> = flow {
        println("Fucking 2")
        emit(ApiStates.Loading)

        try {
            val body = User(
                id = 0L,
                firstName = firstName,
                lastName = lastName,
                account = Account(
                    phone = phone
                )
            )

            val response = userRepository.updateUserProfile(body)
            if (response.status.isSuccess()) {
                val result = response.body<User>()
                println("✅ API Success: $result")
                emit(ApiStates.Success(result))
            } else {
                val errorResponse = response.body<ApiError>()
                println("❌ API Failure: $errorResponse")
                emit(ApiStates.Failure(error = errorResponse))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("🔴 Exception in Flow: ${e.message}")
            emit(ApiStates.Failure(ApiError(message = e.message.orEmpty())))
        }
    }.catch { e ->
        println("🔴 Flow Caught Exception: ${e.message}")
    }

}
