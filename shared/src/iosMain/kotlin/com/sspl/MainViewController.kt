package com.sspl

import androidx.compose.ui.window.ComposeUIViewController
import com.sspl.di.appModules
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(
                appModules +
                        listOf(iosModules)
            )
        }
    }
) {
    App()
}