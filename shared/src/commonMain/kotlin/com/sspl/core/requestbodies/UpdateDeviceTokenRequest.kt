package com.sspl.core.requestbodies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateDeviceTokenRequest(
    @SerialName("device_token") val deviceToken: String,
    @SerialName("device_platform") val devicePlatform: String
)
