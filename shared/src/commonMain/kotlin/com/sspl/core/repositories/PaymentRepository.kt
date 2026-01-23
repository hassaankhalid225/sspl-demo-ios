package com.sspl.core.repositories

import com.sspl.core.apis.PaymentRequest
import com.sspl.core.models.InitiatePaymentRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

class PaymentRepository(private val client: HttpClient) {
    suspend fun initiatePayment(request: InitiatePaymentRequest): HttpResponse =
        client.post(PaymentRequest.Initiate()) {
            setBody(request)
        }
}
