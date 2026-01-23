package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 08/01/2025.
 * se.muhammadimran@gmail.com
 */

@Serializable
enum class SponsorCategory {
    @SerialName("BASIC") BASIC,
    @SerialName("GOLD") GOLD,
    @SerialName("SILVER") SILVER,
    @SerialName("BRONZE") BRONZE,
    @SerialName("PARTNER") PARTNER
}

@Serializable
enum class ReviewStatus {
    @SerialName("PENDING") PENDING,
    @SerialName("ACCEPTED") ACCEPTED,
    @SerialName("REJECTED") REJECTED
}

@Serializable
data class ExhibitionProduct(
    val id: Int? = null,
    val name: String,
    val description: String = "",
    @SerialName("stall_id") val stallId: Int? = null,
    @SerialName("is_delete") val isDelete: Boolean? = null
)

@Serializable
data class ExhibitionStall(
    val id: Int,
    @SerialName("creator_id") val creatorId: Int,
    @SerialName("stall_no") val stallNo: String? = null,
    val category: SponsorCategory = SponsorCategory.BASIC,
    @SerialName("company_name") val companyName: String,
    @SerialName("company_logo") val companyLogo: String = "",
    @SerialName("contact_person") val contactPerson: String,
    @SerialName("contact_num") val contactNum: String = "",
    val email: String,
    val status: ReviewStatus = ReviewStatus.PENDING,
    val products: List<ExhibitionProduct> = emptyList(),
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class ExhibitionListResponse(
    val records: List<ExhibitionStall>,
    val meta: PaginationMeta
)

@Serializable
data class ExhibitionResponse(
    val message: String
)
