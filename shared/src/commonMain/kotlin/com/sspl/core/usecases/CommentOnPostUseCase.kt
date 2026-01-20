package com.sspl.core.usecases

class CommentOnPostUseCase  {
    suspend operator fun invoke(postId: Long):Boolean {
        return true
    }
}