package com.sspl

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import com.sspl.session.UserSession
import com.sspl.theme.MyApplicationTheme
import com.sspl.ui.SplashScreen
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import coil3.network.ktor3.KtorNetworkFetcherFactory

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 01/01/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun App() {
    KoinContext {
        setSingletonImageLoaderFactory { context ->
            getAsyncImageLoader(context)
        }
        MyApplicationTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                MainScreenContent(it)
            }
        }
    }
}

@Composable
private fun MainScreenContent(
    paddingValues: PaddingValues,
    userSession: UserSession = koinInject(),
) {
    val navController = rememberNavController()
    val currentScreen = remember {
        mutableStateOf<Screen>(Screen.SplashScreen)
    }
    
    // Banner state
    val showBannerDialog = remember { mutableStateOf(false) }
    val banners = remember { mutableStateOf<List<com.sspl.core.models.Banner>>(emptyList()) }
    val getBannersUseCase: com.sspl.core.usecases.GetBannersUseCase = koinInject()
    
    LaunchedEffect(Unit) {
        val newScreen = userSession.token?.let {
            Screen.MainScreen
        } ?: Screen.SignInScreen
        currentScreen.value = newScreen
        
        // Fetch banners for all users (Guest & Authenticated)
        getBannersUseCase().collect { state ->
            when (state) {
                is com.sspl.core.ApiStates.Success -> {
                    val activeBanners = state.data?.banners?.filter { it.isActive } ?: emptyList()
                    val welcomeBanner = activeBanners.find { it.type == "WELCOME" }
                    
                    if (welcomeBanner != null) {
                        banners.value = listOf(welcomeBanner)
                        showBannerDialog.value = true
                    }
                }
                else -> {}
            }
        }
    }
    
    // Show banner dialog
    if (showBannerDialog.value && banners.value.isNotEmpty()) {
        com.sspl.ui.components.BannerDialog(
            banners = banners.value,
            baseUrl = com.sspl.utils.BASE_URL,
            onDismiss = { showBannerDialog.value = false }
        )
    }
    
    if(currentScreen.value==Screen.SplashScreen){
        SplashScreen(navController)
    }else {
        NavHost(
            navController = navController,
            startDestination = currentScreen.value.route,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
//                .padding(paddingValues)
        ) {
            composable(route = Screen.MainScreen.route) {
                MainScreen( onParentGraph = {
                    navController.navigate(Screen.SignInScreen.route) {
                        popUpTo(Screen.MainScreen.route) {
                            inclusive = true
                        }
                    }
                })
            }
            kycScreens().forEach { navItem ->
                composable(route = navItem.screen.route) {
                    navItem.component.invoke(navController)
                }
            }
        }
    }
}

fun getAsyncImageLoader(context: PlatformContext) =
    ImageLoader.Builder(context)
        .components {
            add(KtorNetworkFetcherFactory())
        }
        .build()