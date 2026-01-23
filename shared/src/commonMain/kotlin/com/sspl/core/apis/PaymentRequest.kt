package com.sspl.core.apis

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
sealed class PaymentRequest {
    @Serializable
    @Resource("/swich/initiate-payment")
    class Initiate : PaymentRequest()
}
