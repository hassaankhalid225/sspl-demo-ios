package com.sspl.core.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Banner(
    val id: Int,
    val title: String? = null,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("org_id") val orgId: Int? = null,
    val type: String? = "SLIDER",
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String
)

@Serializable
data class BannersResponse(
    val banners: List<Banner>
)
