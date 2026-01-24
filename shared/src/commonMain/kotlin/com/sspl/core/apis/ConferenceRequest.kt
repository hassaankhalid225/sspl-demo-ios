package com.sspl.core.apis

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable


@Serializable
sealed class ConferenceRequest {
    @Serializable
    @Resource("/conferences/{conferenceId}")
    data class ById(
        val conferenceId: Long,
        val format: String = "days"
    ) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}")
    data class ByType(
        val conferenceId: Long,
        val format: String = "days"
    ) : ConferenceRequest()

    @Serializable
    @Resource("/conferences")
    data class GetAll(val type: String? = null) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/sessions/{sessionId}/attendees")
    data class PostAttendance(
        val conferenceId: Long,
        val sessionId: Long,
    ) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/feedback-form")
    data class GetFeedbackForm(
        val conferenceId: Long
    ) : ConferenceRequest()
    @Serializable
    @Resource("/conferences/{conferenceId}/feedback")
    data class PostFeedbackFormRequest(
        val conferenceId: Long
    ) : ConferenceRequest()
    @Serializable
    @Resource("/conferences/{conferenceId}/sessions/{sessionId}/feedback")
    data class PostFeedbackFormSessionRequest(
        val conferenceId: Long,
        val sessionId: Long
    ) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/roles")
    data class ByRoles(val conferenceId: Long) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/registrations/check")
    data class CheckRegistration(val conferenceId: Long) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/register")
    data class RegisterUser(val conferenceId: Long) : ConferenceRequest()

    @Serializable
    @Resource("/conferences/{conferenceId}/registrations")
    data class Registrations(
        val conferenceId: Long,
        val page: Int? = 1,
        val limit: Int? = 20,
        val payment_status: String? = null
    ) : ConferenceRequest()
}