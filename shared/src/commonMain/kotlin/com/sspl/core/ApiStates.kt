package com.sspl.core

import com.sspl.core.models.ApiError


sealed class ApiStates<out T> {
    data class Success<out T>(val data: T?) : ApiStates<T>()
    data class Failure(val error: ApiError) : ApiStates<Nothing>()
    data object Loading : ApiStates<Nothing>()
    data object Idle : ApiStates<Nothing>()
}