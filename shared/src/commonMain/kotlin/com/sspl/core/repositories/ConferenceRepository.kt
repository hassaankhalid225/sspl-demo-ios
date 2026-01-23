package com.sspl.core.repositories

import com.sspl.core.apis.ConferenceRequest
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.serialization.json.JsonObject
import com.sspl.core.models.RegisterUserRequest
// import kotlin.uuid.Uuid removed

class ConferenceRepository(private val client: HttpClient) {

    suspend fun getConferenceById(conferenceId: Long): HttpResponse =
        client.get(ConferenceRequest.ById(conferenceId = conferenceId))

    suspend fun getAllConferences(type: String? = null): HttpResponse =
        client.get(ConferenceRequest.GetAll(type = type))

    suspend fun markAttendanceViaLocation(
        conferenceId: Long,
        sessionId: Long,
    ): HttpResponse =
        client.put(ConferenceRequest.PostAttendance(conferenceId, sessionId))


    suspend fun markAttendanceViaImage(
        conferenceId: Long, sessionId: Long, picture: ByteArray
    ): HttpResponse = client.post(
        ConferenceRequest.PostAttendance(conferenceId, sessionId)
    ) {
        setBody(
            MultiPartFormDataContent(
            formData {
                append("picture", picture, Headers.build {
                    append(HttpHeaders.ContentType, "application/octet-stream")
                    append(HttpHeaders.ContentDisposition, "filename=\"image.jpg\"")
                })
            }
        ))
    }

    suspend fun getFeedbackForm(
        conferenceId: Long
    ): HttpResponse = client.get(
        ConferenceRequest.GetFeedbackForm(conferenceId)
    )
    suspend fun postFeedbackForm(
        conferenceId: Long,body: JsonObject
    ): HttpResponse = client.post(
        ConferenceRequest.PostFeedbackFormRequest(conferenceId)
    ){
        setBody(body)
    }
    suspend fun postFeedbackFormSession(
        conferenceId: Long,body: JsonObject,sessionId: Long
    ): HttpResponse = client.post(
         ConferenceRequest.PostFeedbackFormSessionRequest(conferenceId,sessionId)
    ){
        setBody(body)
    }

    suspend fun checkRegistration(conferenceId: Long): HttpResponse =
        client.get(ConferenceRequest.CheckRegistration(conferenceId))

    suspend fun registerUser(conferenceId: Long, request: RegisterUserRequest): HttpResponse =
        client.post(ConferenceRequest.RegisterUser(conferenceId)) {
            setBody(request)
        }

    suspend fun getConferenceRoles(conferenceId: Long): HttpResponse =
        client.get(ConferenceRequest.ByRoles(conferenceId))

    suspend fun getUserRegistrations(
        conferenceId: Long,
        page: Int = 1,
        limit: Int = 20,
        paymentStatus: String? = null
    ): HttpResponse =
        client.get(ConferenceRequest.Registrations(conferenceId, page, limit, paymentStatus))
}