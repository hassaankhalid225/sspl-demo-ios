package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.resources.Res
import com.sspl.resources.demo1
import com.sspl.resources.demo2
import com.sspl.resources.demo3
import com.sspl.resources.demo4

import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.compose.resources.DrawableResource

class GetPostDetailsUseCase(
       val httpClient: HttpClient   //demo -> to be replaced with some repo ..
) {
    operator fun invoke(id:Long) : Flow<ApiStates<List<Comment>>> = flow {
        emit(ApiStates.Loading)
        println(id.toString())
        delay(1500)
        emit(ApiStates.Success(sampleComments))
    }
}

data class Comment(
    val userName: String,
    val userAvatar: DrawableResource,
    val content: String,
)
var sampleComments = listOf(
    Comment(
        userName = "John Doe",
        userAvatar = Res.drawable.demo2,
        content = "This is a great post! Thanks for sharing. I have a question about this. Can you elaborate more on the second point?",

    ), Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo3,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo4,
        content = "I have a question about this. Can you ela I have a question about this. Can you elaborate more on the second point? borate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo1,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo2,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo3,
        content = "I have a question about this. I have a question about this. Can you elaborate more on the second point? Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo4,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo1,
        content = "I have a question about this. Can you elaborate more on the second point? I have a question about this. Can you elaborate more on the second point? I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo2,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ),Comment(
        userName = "Jane Smith",
        userAvatar = Res.drawable.demo3,
        content = "I have a question about this. Can you elaborate more on the second point?",


    ), Comment(
        userName = "Bob Johnson",
        userAvatar = Res.drawable.demo4,
        content = "Interesting perspective. I hadn't thought about it that way before.",


    )
)