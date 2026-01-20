package com.sspl.platform

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIScreen
import platform.CoreGraphics.CGRectGetHeight
import platform.CoreGraphics.CGRectGetWidth

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 01/02/2025.
 * se.muhammadimran@gmail.com
 */
@OptIn(ExperimentalForeignApi::class)
class IosConfiguration : PlatformConfiguration {

    override val screenWidth: Dp
        get() = CGRectGetWidth(UIScreen.mainScreen.bounds).dp
    override val screenHeight: Dp
        get() = CGRectGetHeight(UIScreen.mainScreen.bounds).dp
}