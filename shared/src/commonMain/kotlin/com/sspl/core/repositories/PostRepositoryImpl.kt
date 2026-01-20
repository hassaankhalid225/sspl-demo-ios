package com.sspl.core.repositories

import com.sspl.core.models.Post

import com.sspl.utils.dummyPostList
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PostRepositoryImpl(
    val httpClient: HttpClient
) : PostsRepository {
    override suspend fun getPosts(): Flow<List<Post>> {
        return flow {
            delay(500) // Simulating network delay for demonstration purposes
            emit(dummyPostList)
//            emit(emptyList())
        }
    }

}