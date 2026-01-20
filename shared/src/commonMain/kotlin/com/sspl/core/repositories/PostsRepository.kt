package com.sspl.core.repositories

import com.sspl.core.models.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    suspend fun getPosts():  Flow<List<Post>>

}