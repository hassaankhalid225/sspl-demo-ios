package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantResponse(
    val id: Int,
    @SerialName("session_id") val sessionId: Int,
    val name: String,
    val email: String,
    @SerialName("joined_at") val joinedAt: String,
    @SerialName("join_url") val joinUrl: String? = null
)
