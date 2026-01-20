package com.sspl.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sspl.core.ApiStates
import com.sspl.core.models.Banner
import com.sspl.core.usecases.GetBannersUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import com.sspl.core.push.PushNotificationService

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 30/01/2025.
 * se.muhammadimran@gmail.com
 */
class HomeScreenViewModel(
    private val getBannersUseCase: GetBannersUseCase,
    private val pushNotificationService: PushNotificationService
) : ViewModel() {
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners = _banners.asStateFlow()

    init {
        fetchBanners()
        viewModelScope.launch {
            pushNotificationService.registerDeviceToken()
        }
    }


    fun fetchBanners() {
        viewModelScope.launch {
            println(">>> Banners: Fetching banners...")
            getBannersUseCase().collect { state ->
                when (state) {
                    is ApiStates.Success -> {
                        val activeBanners = state.data?.banners?.filter { it.isActive } ?: emptyList()
                        println(">>> Banners: Success! Received ${activeBanners.size} active banners")
                        _banners.value = activeBanners
                    }
                    is ApiStates.Failure -> {
                        println(">>> Banners: Failed to fetch! Error: ${state.error.message}")
                    }
                    is ApiStates.Loading -> {
                        println(">>> Banners: Loading...")
                    }
                    else -> {}
                }
            }
        }
    }
}