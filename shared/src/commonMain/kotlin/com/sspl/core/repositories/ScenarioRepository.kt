package com.sspl.core.repositories

import com.sspl.core.apis.ScenarioRequest
import com.sspl.core.models.ParticipantResponse
import com.sspl.core.requestbodies.JoinSessionRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ScenarioRepository(private val httpClient: HttpClient) {
    
    fun joinSession(
        joinCode: String,
        name: String,
        email: String,
        deviceToken: String?,
        devicePlatform: String?
    ): Flow<ParticipantResponse> = flow {
        val response = httpClient.post(ScenarioRequest.Join(joinCode = joinCode)) {
            setBody(
                JoinSessionRequest(
                    name = name,
                    email = email,
                    deviceToken = deviceToken,
                    devicePlatform = devicePlatform
                )
            )
        }.body<ParticipantResponse>()
        emit(response)
    }
    
    /**
     * Auto-join session using authentication token
     * No name/email required - extracted from token by backend
     */
    fun autoJoinSession(joinCode: String): Flow<ParticipantResponse> = flow {
        val response = httpClient.post(ScenarioRequest.AutoJoin(joinCode = joinCode))
            .body<ParticipantResponse>()
        emit(response)
    }
}
