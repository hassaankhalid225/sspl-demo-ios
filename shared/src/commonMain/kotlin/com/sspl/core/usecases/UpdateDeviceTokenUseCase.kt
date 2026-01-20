package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.repositories.UserRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

@Serializable
data class DeviceTokenResponse(
    val message: String? = null
)

class UpdateDeviceTokenUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(
        deviceToken: String,
        devicePlatform: String
    ): Flow<ApiStates<DeviceTokenResponse>> = flow {
        emit(ApiStates.Loading)
        try {
            val response = userRepository.updateDeviceToken(deviceToken, devicePlatform)
            if (response.status.isSuccess()) {
                val result = response.body<DeviceTokenResponse>()
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
