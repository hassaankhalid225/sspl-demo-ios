package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 08/01/2025.
 * se.muhammadimran@gmail.com
 */

@Serializable
data class SessionNotification(
    @SerialName("session_id") val sessionId: Int,
    @SerialName("scenario_id") val scenarioId: Int? = null,
    @SerialName("scenario_title") val scenarioTitle: String? = null,
    @SerialName("join_code") val joinCode: String,
    @SerialName("join_url") val joinUrl: String? = null,
    @SerialName("qr_code") val qrCode: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("current_question_index") val currentQuestionIndex: String? = null,
    @SerialName("time_per_question") val timePerQuestion: String? = null
)

@Serializable
data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.SESSION_LIVE,
    val sessionNotification: SessionNotification? = null
)

@Serializable
enum class NotificationType {
    SESSION_LIVE,
    GENERAL,
    ANNOUNCEMENT
}
