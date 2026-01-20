package com.sspl.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.PlatformLocale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspl.platform.platformConfiguration

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = lightScheme
//        if (darkTheme) {
//        darkColorScheme(
//            primary = Color(0xFFBB86FC),
//            secondary = Color(0xFF03DAC5),
//            tertiary = Color(0xFF3700B3)
//        )
//    } else {
//        lightColorScheme(
//            primary = Color(0xFF6200EE),
//            secondary = Color(0xFF03DAC5),
//            tertiary = Color(0xFF3700B3)
//        )
//    }
    val fontFamily = AppFontFamily()
    val typography1 = Typography(
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val typography2 = AppTypography()
//    val typography = typography(AppFontFamily())
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    CompositionLocalProvider(
        LocalAppConfig provides platformConfiguration,
    ) {
        MaterialTheme(
            colorScheme = colors,
//        typography = typography1,
            typography = typography2,
            shapes = shapes,
            content = content
        )

    }

}

private val platformConfiguration = platformConfiguration()
private val LocalAppConfig = compositionLocalOf {
    platformConfiguration
}
val MaterialTheme.platformConfig
    @Composable
    get() = LocalAppConfig.current
