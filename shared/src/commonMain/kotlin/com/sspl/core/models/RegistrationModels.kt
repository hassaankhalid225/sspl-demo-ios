package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: Long,
    val name: String,
    val fee: Double,
    val description: String? = null
)

@Serializable
data class Registration(
    val id: Long,
    @SerialName("person_id") val personId: Long? = null,
    @SerialName("conference_id") val conferenceId: Long? = null,
    @SerialName("role_id") val roleId: Long? = null,
    @SerialName("fee_amount") val feeAmount: Double,
    @SerialName("payment_status") val paymentStatus: String, // PENDING | SUCCESS | FAILED
    val role: Role? = null,
    @SerialName("payment_date") val paymentDate: String? = null
)

@Serializable
data class CheckRegistrationResponse(
    @SerialName("is_registered") val isRegistered: Boolean,
    val registration: Registration?
)

@Serializable
data class RegisterUserRequest(
    @SerialName("role_id") val roleId: Long
)

@Serializable
data class RegisterUserResponse(
    val message: String,
    val registration: Registration
)

@Serializable
data class UpdatePaymentRequest(
    @SerialName("payment_status") val paymentStatus: String,
    @SerialName("payment_mode") val paymentMode: String? = null,
    @SerialName("bank_name") val bankName: String? = null,
    @SerialName("transaction_id") val transactionId: String? = null
)

@Serializable
data class UpdatePaymentResponse(
    val message: String,
    val registration: Registration
)
