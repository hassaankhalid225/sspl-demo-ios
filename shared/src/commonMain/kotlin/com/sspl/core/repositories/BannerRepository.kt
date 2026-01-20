package com.sspl.core.repositories

import com.sspl.core.apis.BannerRequest
import com.sspl.core.models.BannersResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BannerRepository(private val httpClient: HttpClient) {
    
    /**
     * Fetch all active banners for the user's organization and global banners
     * Requires authentication token
     */
    fun getBanners(): Flow<BannersResponse> = flow {
        val jsonString = httpClient.get(BannerRequest()).body<String>()
        println(">>> Banners: Raw JSON: $jsonString")
        
        val banners = try {
            // Try parsing as BannersResponse object: { "banners": [...] }
            kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<BannersResponse>(jsonString).banners
        } catch (e: Exception) {
            println(">>> Banners: Failed to parse as BannersResponse, trying as direct List<Banner>")
            try {
                // Try parsing as direct list: [ { ... }, { ... } ]
                kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.decodeFromString<List<com.sspl.core.models.Banner>>(jsonString)
            } catch (e2: Exception) {
                println(">>> Banners: Failed to parse directly. Error: ${e2.message}")
                emptyList()
            }
        }
        
        emit(BannersResponse(banners))
    }
}
