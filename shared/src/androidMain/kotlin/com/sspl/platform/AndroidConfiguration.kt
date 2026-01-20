package com.sspl.platform

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 01/02/2025.
 * se.muhammadimran@gmail.com
 */
class AndroidConfiguration : PlatformConfiguration {
    override val screenWidth: Dp
        get() = (Resources.getSystem().displayMetrics.widthPixels
                / Resources.getSystem().displayMetrics.density).dp
    override val screenHeight: Dp
        get() = (Resources.getSystem().displayMetrics.heightPixels
                / Resources.getSystem().displayMetrics.density).dp
}
