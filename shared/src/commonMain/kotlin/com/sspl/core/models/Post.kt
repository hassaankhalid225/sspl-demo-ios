package com.sspl.core.models

data class Post(
    val postId: Long,
    val userName: String?,
    val userProfileUrl: String?,
    val description: String?,
    val images: List<String>?,
    val likes: Int?,
    val dislikes: Int?,
    val commentCount: Int?,
    val disLikeCount: Int?,
)