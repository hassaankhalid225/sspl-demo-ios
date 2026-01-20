package com.sspl.core.repositories

import com.sspl.core.apis.AuthRequest
import com.sspl.core.apis.ProfileRequest
import com.sspl.core.models.Account
import com.sspl.core.models.InitResetPassword
import com.sspl.core.models.UpdatePasswordRequest
import com.sspl.core.models.User
import com.sspl.core.requestbodies.DeleteAccountBody
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 21/01/2025.
 * se.muhammadimran@gmail.com
 */

class AuthRepository(private val client: HttpClient) {
    suspend fun login(body: Account): HttpResponse =
        client.post(AuthRequest.Login()) {
            setBody(body)
        }

    suspend fun signUp(body: User): HttpResponse =
        client.post(AuthRequest.SignUp()) {
            setBody(body)
        }

    suspend fun initResetPassword(body: InitResetPassword): HttpResponse =
        client.post(AuthRequest.ResetPasswordInit()) {
            setBody(body)
        }

    suspend fun updatePassword(body: UpdatePasswordRequest): HttpResponse =
        client.post(AuthRequest.UpdatePassword()) {
            setBody(body)
        }

    suspend fun deleteAccount(body: DeleteAccountBody): HttpResponse =
        client.post(AuthRequest.DeleteAccount()) {
            setBody(body)
        }
}