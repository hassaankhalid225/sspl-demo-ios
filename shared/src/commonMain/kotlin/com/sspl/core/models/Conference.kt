package com.sspl.core.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Conference(
    val id: Long,
    val type: ConferenceType,
    val title: String?,
    val summary: String?,
    @SerialName("start_date") val startDate: String?,
    @SerialName("end_date") val endDate: String?,
    @SerialName("created_at") val createdAt: String?,
    @SerialName("updated_at") val updatedAt: String?,
    @SerialName("venue") val venue: Venue? = null
) {
    fun isWorkshop(): Boolean = type == ConferenceType.WORKSHOP
}

@Serializable
enum class ConferenceType {
    ALL,
    WORKSHOP,
    PROGRAM
}
