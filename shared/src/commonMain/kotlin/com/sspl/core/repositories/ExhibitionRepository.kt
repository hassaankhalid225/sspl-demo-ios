package com.sspl.core.repositories

import com.sspl.core.apis.ExhibitionRequest
import com.sspl.core.models.ExhibitionListResponse
import com.sspl.core.models.ExhibitionProduct
import com.sspl.core.models.ExhibitionResponse
import com.sspl.core.models.ExhibitionStall
import com.sspl.core.models.SponsorCategory
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 06/01/2025.
 * se.muhammadimran@gmail.com
 */

class ExhibitionRepository(private val client: HttpClient) {

    /**
     * Create a new exhibition stall with multipart form data
     * @param companyName Name of the company
     * @param contactPerson Name of the contact person
     * @param email Contact email address
     * @param contactNum Contact phone number (optional)
     * @param stallNo Stall number/identifier (optional)
     * @param category Sponsorship category (optional, default: BASIC)
     * @param companyLogo Company logo image bytes (optional)
     * @param logoFileName Logo file name (optional)
     * @param products List of products (optional)
     */
    suspend fun createExhibitionStall(
        companyName: String,
        contactPerson: String,
        email: String,
        contactNum: String = "",
        stallNo: String = "",
        category: SponsorCategory = SponsorCategory.BASIC,
        companyLogo: ByteArray? = null,
        logoFileName: String? = null,
        products: List<ExhibitionProduct> = emptyList()
    ): ExhibitionResponse {
        return client.submitFormWithBinaryData(
            url = "/exhibitionStall/create",
            formData = formData {
                append("company_name", companyName)
                append("contact_person", contactPerson)
                append("email", email)
                if (contactNum.isNotBlank()) append("contact_num", contactNum)
                if (stallNo.isNotBlank()) append("stall_no", stallNo)
                append("category", category.name)
                
                if (companyLogo != null && logoFileName != null) {
                    append("company_logo", companyLogo, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$logoFileName\"")
                    })
                }
                
                if (products.isNotEmpty()) {
                    append("products", Json.encodeToString(products))
                }
            }
        ).body()
    }

    /**
     * Get a single exhibition stall by ID
     * @param stallId ID of the exhibition stall
     */
    suspend fun getExhibitionStall(stallId: Int): ExhibitionStall {
        return client.get(ExhibitionRequest.GetSingle(stall_id = stallId)).body()
    }

    /**
     * Get paginated list of exhibition stalls
     * @param page Page number (1-indexed)
     * @param limit Number of records per page
     * @param status Filter by review status (optional)
     */
    suspend fun getExhibitionStalls(
        page: Int = 1,
        limit: Int = 20,
        status: String? = null
    ): ExhibitionListResponse {
        return client.get(ExhibitionRequest()) {
            parameter("page", page)
            parameter("limit", limit)
            status?.let { parameter("status", it) }
        }.body()
    }

    /**
     * Update an existing exhibition stall
     * @param stallId ID of the exhibition stall to update
     * @param companyName Updated company name (optional)
     * @param contactPerson Updated contact person (optional)
     * @param email Updated email address (optional)
     * @param contactNum Updated contact number (optional)
     * @param stallNo Updated stall number (optional)
     * @param category Updated category (optional)
     * @param companyLogo New logo image bytes (optional)
     * @param logoFileName Logo file name (optional)
     * @param removeLogo Set to true to remove existing logo
     * @param products Updated list of products with operations (optional)
     */
    suspend fun updateExhibitionStall(
        stallId: Int,
        companyName: String? = null,
        contactPerson: String? = null,
        email: String? = null,
        contactNum: String? = null,
        stallNo: String? = null,
        category: SponsorCategory? = null,
        companyLogo: ByteArray? = null,
        logoFileName: String? = null,
        removeLogo: Boolean = false,
        products: List<ExhibitionProduct>? = null
    ): ExhibitionResponse {
        return client.submitFormWithBinaryData(
            url = "/exhibitionStall/update/$stallId",
            formData = formData {
                companyName?.let { append("company_name", it) }
                contactPerson?.let { append("contact_person", it) }
                email?.let { append("email", it) }
                contactNum?.let { append("contact_num", it) }
                stallNo?.let { append("stall_no", it) }
                category?.let { append("category", it.name) }
                
                if (removeLogo) {
                    append("company_logo", "")
                } else if (companyLogo != null && logoFileName != null) {
                    append("company_logo", companyLogo, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$logoFileName\"")
                    })
                }
                
                products?.let {
                    append("products", Json.encodeToString(it))
                }
            }
        ) {
            method = HttpMethod.Put
        }.body()
    }

    /**
     * Delete an exhibition stall
     * @param exhibitionId ID of the exhibition stall to delete
     */
    suspend fun deleteExhibitionStall(exhibitionId: Int): ExhibitionResponse {
        return client.delete(ExhibitionRequest.Delete(exhibition_id = exhibitionId)).body()
    }
}
