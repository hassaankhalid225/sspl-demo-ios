package com.sspl.utils


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalDensity
import coil3.PlatformContext
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */


@Composable
fun appImageRequest(url: String, context: PlatformContext) = ImageRequest.Builder(context)
    .data(url)
    .diskCacheKey(url)
    .memoryCacheKey(url)
    .diskCachePolicy(CachePolicy.ENABLED)
    .memoryCachePolicy(CachePolicy.ENABLED)
//    .error(Res.drawable.app_banner)
//    .fallback(Res.drawable.app_banner)
    .crossfade(true)
    .build()


///**
// * Format view to like 2.4K, 4M
// */
//fun Long.formatViews(): String {
//    if (this < 1000)
//        return "" + this
//    val exp = (Math.log(this.toDouble()) / Math.log(1000.0)).toInt()
//    return String.format(
//        Locale.getDefault(),
//        "%.1f%c",
//        this / Math.pow(1000.0, exp.toDouble()),
//        "kMGTPE"[exp - 1]
//    )
//}

fun <T1, T2, R> combineState(
    flow1: StateFlow<T1>,
    flow2: StateFlow<T2>,
    scope: CoroutineScope,
    sharingStarted: SharingStarted = SharingStarted.Eagerly,
    transform: (T1, T2) -> R
): StateFlow<R> = combine(flow1, flow2) { o1, o2 ->
    transform.invoke(o1, o2)
}.stateIn(scope, sharingStarted, transform.invoke(flow1.value, flow2.value))

inline fun startCoroutineTimer(
    delayMillis: Long = 0,
    repeatMillis: Long = 0,
    scope: CoroutineScope,
    crossinline action: () -> Unit
) = scope.launch {
    delay(delayMillis)
    if (repeatMillis > 0) {
        while (true) {
            action()
            delay(repeatMillis)
        }
    } else {
        action()
    }
}

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
