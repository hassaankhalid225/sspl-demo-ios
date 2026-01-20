package com.sspl.core.models

import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 21/01/2025.
 * se.muhammadimran@gmail.com
 */
@Serializable
data class ApiError(val message: String, val error: String? = null) {
    override fun toString(): String {
        return "$message\n$error"
    }
}
