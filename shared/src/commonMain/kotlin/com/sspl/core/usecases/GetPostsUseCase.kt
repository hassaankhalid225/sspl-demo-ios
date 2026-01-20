package com.sspl.core.usecases

import com.sspl.core.models.Post
import com.sspl.core.repositories.PostsRepository
import kotlinx.coroutines.flow.Flow

class GetPostsUseCase(
    private val postRepository: PostsRepository
) {
    suspend operator fun invoke(): Flow<List<Post>> = postRepository.getPosts()
}