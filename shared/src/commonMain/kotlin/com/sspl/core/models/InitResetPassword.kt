package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitResetPassword(
    @SerialName("email") val email: String?
)

@Serializable
data class PasswordToken(
    @SerialName("token") val token: String?
)


@Serializable
data class UpdatePasswordRequest(
    val code: String, val password: String, val token: String
)