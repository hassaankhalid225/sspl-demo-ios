package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 12/02/2025.
 * se.muhammadimran@gmail.com
 */

@Serializable
data class FeedbackForm(
    val id: Long,
    val title: String,
    val description: String? = null,
    @SerialName("conference_id") val conferenceId: Long,
    @SerialName("session_id")
    val sessionId: Long? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("question_groups") val questionGroups: List<QuestionGroup>
)

@Serializable
data class QuestionGroup(
    val title: String,
    val description: String,
    val questions: List<Question>
)

@Serializable
data class Question(
    val id: Int,
    val statement: String,
    val description: String? = null,
    val type: QuestionType,
    val options: List<Option>,
    val statements: List<Statement>
)

@Serializable
data class Option(
    val label: String,
    val value: String
)

@Serializable
data class Statement(
    val id: Int,
    val statement: String
)

enum class QuestionType {
    EVALUATION,
    COMMENT,
    YESNO
}