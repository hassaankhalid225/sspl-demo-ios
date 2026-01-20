package com.sspl.core.apis

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 21/01/2025.
 * se.muhammadimran@gmail.com
 */
@Serializable
@Resource("/auth")  
object AuthRequest {
    @Serializable
    @Resource("/signup")
    data class SignUp(val parent: AuthRequest = AuthRequest)

    @Serializable
    @Resource("/login")
    data class Login(val parent: AuthRequest = AuthRequest)

    @Serializable
    @Resource("/delete-account/init")
    data class DeleteAccount(val parent: AuthRequest = AuthRequest)

    @Serializable
    @Resource("/reset-password/init")
    data class ResetPasswordInit(val parent: AuthRequest = AuthRequest)

    @Serializable
    @Resource("/reset-password")
    data class UpdatePassword(val parent: AuthRequest = AuthRequest)
}
