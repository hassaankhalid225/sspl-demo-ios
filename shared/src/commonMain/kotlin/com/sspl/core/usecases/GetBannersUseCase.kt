package com.sspl.core.usecases

import com.sspl.core.ApiStates
import com.sspl.core.models.ApiError
import com.sspl.core.models.BannersResponse
import com.sspl.core.repositories.BannerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class GetBannersUseCase(private val bannerRepository: BannerRepository) {
    
    operator fun invoke(): Flow<ApiStates<BannersResponse>> = flow {
        emit(ApiStates.Loading)
        bannerRepository.getBanners()
            .map { 
                ApiStates.Success(it)  
            }
            .catch { e ->
                emit(ApiStates.Failure(ApiError(message = e.message ?: "Failed to load banners")))
            }
            .collect {
                emit(it)
            }
    }
}
