package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 10/02/2025.
 * se.muhammadimran@gmail.com
 */
@Serializable
data class CommonResponse(
    val message: String
)

@Serializable
@SerialName("meta")
data class PaginationMeta(
    @SerialName("total_records") val totalRecords: Int,
    @SerialName("current_page") val currentPage: Int,
    @SerialName("total_pages") val totalPages: Int,
    val limit: Int
)
