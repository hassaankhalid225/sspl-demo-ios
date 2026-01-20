package com.sspl.core.usecases

class DislikePostUseCase() {
    suspend operator fun invoke(postId: Long):Boolean {
         return true
    }
}