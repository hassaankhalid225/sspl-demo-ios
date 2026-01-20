package com.sspl.core.requestbodies

import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
@Serializable
data class DeleteAccountBody(val email: String)
