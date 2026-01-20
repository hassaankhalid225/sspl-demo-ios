package com.sspl.core.usecases

class LikePostUseCase(

)  {
    suspend operator fun invoke(postId: Long):Boolean {

        return true
    }
}