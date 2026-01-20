package com.sspl.core.apis

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable

object ProfileRequest {

    @Serializable
    @Resource("/profiles/me")
    data object UserProfileRequest

    @Serializable
    @Resource("/profiles/me")
    data object GetUserProfile

    @Serializable
    @Resource("/users/device-token")
    data object UpdateDeviceToken
}
