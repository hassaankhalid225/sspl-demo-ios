package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    val id: Long,
    val name: String,
    val fee: Double, // API sends Int ("fee": 5000) but Double is safer for money
    val description: String? = null,
    @SerialName("conference_id") val conferenceId: Long? = null,
    @SerialName("active_status") val activeStatus: String? = null
)

@Serializable
data class Registration(
    val id: Long,
    @SerialName("person_id") val personId: Long? = null,
    @SerialName("conference_id") val conferenceId: Long? = null,
    @SerialName("role_id") val roleId: Long? = null,
    @SerialName("fee_amount") val feeAmount: Double,
    @SerialName("payment_status") val paymentStatus: String, // PENDING | SUCCESS | FAILED | REFUNDED
    @SerialName("payment_mode") val paymentMode: String? = null, // CASH | BANK | CARD | null
    @SerialName("transaction_id") val transactionId: String? = null,
    @SerialName("payment_date") val paymentDate: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    val role: Role? = null,
    val conference: Conference? = null // For "My Registrations" list
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
    val message: String? = null,
    val registration: Registration? = null,
    val error: String? = null
)

@Serializable
data class RegistrationListResponse(
    val records: List<Registration>,
    @SerialName("_meta") val meta: PaginationMeta? = null
)

@Serializable
data class InitiatePaymentRequest(
    @SerialName("item_type") val itemType: String = "conference_registration",
    @SerialName("conference_registration_id") val conferenceRegistrationId: Long? = null,
    @SerialName("membership_id") val membershipId: Long? = null,
    val amount: Double? = null,
    val item: String? = null,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val userId: Long? = null
)

@Serializable
data class InitiatePaymentResponse(
    @SerialName("payment_url") val paymentUrlSnake: String? = null,
    @SerialName("customer_transaction_id") val customerTransactionId: String? = null,
    val amount: Double? = null,
    val item: String? = null,
    
    // Legacy / Alternative format support
    val success: Boolean? = null,
    val paymentUrl: String? = null,
    val transactionId: String? = null
) {
    val bestPaymentUrl: String?
        get() = paymentUrlSnake ?: paymentUrl
}
