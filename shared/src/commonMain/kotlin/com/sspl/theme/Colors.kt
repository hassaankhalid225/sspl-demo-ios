package com.sspl.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */
val primary = Color(0xFF0A528F)
val secondary = Color(0xFF053762)
val tertiary = Color(0xFF032746)
val windowBackGround = Color(0xFFFFFFFF)//0xFFF6F6F6
val dividerColor = Color(0xFFF6F5F5)
val columnColor = Color(0xFFDBE3EE)
val columnColorLight = Color(0xFFE1E8F1)
val boxColor = Color(0xFFEBF1F6)
val boxColorLight = Color(0xFFF2F7FC)
val green = Color(0xFF0A520F)
val orange = Color(0xFFC27705)
val textColor = Color(0xFF1E1E1E)

val lightScheme = lightColorScheme(
    primary = primary,
    primaryContainer = primary,
    onPrimaryContainer = Color.White,
    onPrimary = textColor,
    secondary = secondary,
    onSecondary =  Color.White,
    tertiary = tertiary,
    onTertiary =  Color.White,
    background = primary,
    onBackground = textColor,
)