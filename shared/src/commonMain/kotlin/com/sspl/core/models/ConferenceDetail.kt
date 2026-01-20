package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConferenceDetail(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: String?,
    @SerialName("title") val title: String?,
    @SerialName("summary") val summary: String?,
    @SerialName("start_date") val startDate: String?,
    @SerialName("end_date") val endDate: String?,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("add_on_sets") val addOnSets: List<AddOnSet>?,
    @SerialName("days") val days: List<Day>?,
    @SerialName("roles") val roles: List<Role>? = null,
    @SerialName("venue") val venue: Venue? = null
)

@Serializable
data class AddOnSet(
    @SerialName("title") val title: String?,
    @SerialName("add_ons") val addOns: List<AddOn>?
)

@Serializable
data class AddOn(
    @SerialName("title") val title: String?,
    @SerialName("subtitle") val subtitle: String?
)

@Serializable
data class Day(
    @SerialName("date") val date: String,
    @SerialName("breaks") val breaks: List<Session>? = null,
    @SerialName("sessions") val sessions: List<Session>? = null
)

@Serializable
data class Break(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String?,
    @SerialName("summary") val summary: String?,
    @SerialName("venue_id") val venueId: Int?,
    @SerialName("web_link") val webLink: String?,
    @SerialName("is_break") val isBreak: Boolean,
    @SerialName("starts_at") val startsAt: String?,
    @SerialName("ends_at") val endsAt: String?,
    @SerialName("add_on_sets") val addOnSets: List<AddOnSet>?,
    @SerialName("breakdown") val breakdown: List<Breakdown>?,
    @SerialName("venue") val venue: Venue?
)

@Serializable
data class Session(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("summary") val summary: String?,
    @SerialName("venue_id") val venueId: Int?,
    @SerialName("web_link") val webLink: String?,
    @SerialName("is_break") val isBreak: Boolean,
    @SerialName("starts_at") val startsAt: String?,
    @SerialName("ends_at") val endsAt: String?,
    @SerialName("add_on_sets") val addOnSets: List<AddOnSet>?,
    @SerialName("breakdown") val breakdown: List<Breakdown>?,
    @SerialName("venue") val venue: Venue?
)

@Serializable
data class Speaker(
    @SerialName("id") val id: Long,
    @SerialName("email") val email: String,
    @SerialName("prefix") val prefix: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    @SerialName("gender") val gender: String,
) {
    fun fullName() = "$prefix $firstName $lastName"
}

@Serializable
data class Venue(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("address") val address: String?,
    @SerialName("lat") val lat: Double?,
    @SerialName("lng") val lng: Double?,
    @SerialName("radius") val radius: Int?
)

@Serializable
data class Breakdown(
    @SerialName("title") val title: String?,
    @SerialName("starts_at") val startsAt: String?,
    @SerialName("ends_at") val endsAt: String?,
    @SerialName("is_break") val isBreak: Boolean,
    @SerialName("speakers") val speakers: List<Speaker>,
)