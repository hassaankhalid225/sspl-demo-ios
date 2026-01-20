package com.sspl.core.requestbodies

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class FeedbackBody(
    val data: MutableSet<FeedbackBodyItem>
){
    fun put(feedbackBodyItem: FeedbackBodyItem){
        data.add(feedbackBodyItem)
    }

}
@kotlinx.serialization.Serializable
data class FeedbackBodyItem(
    @SerialName("question_id")
    val questionId: Long,
    val value: String
){
    override fun equals(other: Any?): Boolean {
        if(other is FeedbackBodyItem){
            return questionId == other.questionId
        }
        return false

    }
}
