package com.sspl.core.models


import com.sspl.utils.EMAIL_GUEST_USER
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("access_token")
    val accessToken: String?,

    @SerialName("user")
    val user: User?
)

@Serializable
data class User(
    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    val gender: String? = null,
    val role: String? = null,

    @SerialName("profile")
    val profile: Profile? = null,

    @SerialName("account")
    val account: Account? = null
) {
    fun fullName() = "$firstName $lastName"
}

@Serializable
data class Profile(
    val title: String? = null,
    val overview: String? = null,
    @SerialName("pmdc_number")
    val pmdcNumber: String? = null,
    val institute: String? = null,
    val country: String? = null,

    @SerialName("org_number")
    val orgNumber: String? = null,

    @SerialName("contact")
    val contact: Contact? = null
)

@Serializable
data class Contact(
    val whatsapp: String?,
    val skype: String?,
    val twitter: String?
)

@Serializable
data class Account(
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null
) {
    fun isGuestUser() = email?.endsWith(EMAIL_GUEST_USER) == true
}

