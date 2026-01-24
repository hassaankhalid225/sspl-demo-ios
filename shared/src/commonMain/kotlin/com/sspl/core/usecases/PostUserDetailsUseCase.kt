package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.Account
import com.sspl.core.models.ApiError
import com.sspl.core.models.Profile
import com.sspl.core.models.User
import com.sspl.core.models.UserResponse
import com.sspl.core.repositories.UserRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.flow.Flow

class PostUserDetailsUseCase(
    private val userRepository: UserRepository
) {

    operator fun invoke(
        pmdcNo: String? = null,
        instituteName: String? = null,
        country: String? = null,
        designation: String? = null,
        registrationNo: String? = null,
    ) = flow<ApiStates<User>> {
        emit(ApiStates.Loading)
        try {
            val body = User(
                id = 0L,
                profile = Profile(
                    country = country,
                    institute = instituteName,
                    title = designation,
                    pmdcNumber = pmdcNo, 
                    orgNumber = registrationNo
                )
            )

            val response = userRepository.updateUserProfile(body)
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
