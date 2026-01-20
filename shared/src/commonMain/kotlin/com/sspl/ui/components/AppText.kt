package com.sspl.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.sspl.theme.AppFontFamily
import com.sspl.theme.textColor
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.withStyle


/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/01/2025.
 * se.muhammadimran@gmail.com
 */
@Composable
fun AppText(
    text: String,
    modifier: Modifier,
    style: TextStyle,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = 18.sp
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        lineHeight = lineHeight,
        textDecoration = textDecoration
    )
}

@Composable
fun AppTextTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.tertiary,
    fontSize: TextUnit = 16.sp,
    fontFamily: FontFamily = AppFontFamily(),
    style: TextStyle = MaterialTheme.typography.titleLarge.copy(
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = fontSize,
        fontFamily = fontFamily
    ),
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = 18.sp
) {
    AppText(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        textDecoration = textDecoration,
        lineHeight = lineHeight
    )
}

@Composable
fun AppTextSubTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.secondary,
    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = AppFontFamily(),
    style: TextStyle = MaterialTheme.typography.titleLarge.copy(
        color = color,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize,
        fontFamily = fontFamily
    ),
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = 18.sp,
    isAllCapsStyled: Boolean = false,
    fontSizeCap: TextUnit = 15.sp,
) {
    if (isAllCapsStyled) {
        val annotatedString = AnnotatedString.Builder().apply {
            val words = text.split(" ")
            words.forEachIndexed { i, word ->
                val firstLetter = word.firstOrNull()?.uppercase() ?: ""
                val remainingText = word.drop(1).uppercase()
                withStyle(
                    style = style.toSpanStyle().copy(fontSize = fontSizeCap)
                ) {
                    append(firstLetter)
                }
                withStyle(
                    style = style.toSpanStyle()
                ) {
                    append(remainingText)
                }
                if (i < words.size - 1) {
                    append(" ")
                }
            }
        }.toAnnotatedString()
        Text(
            modifier = modifier,
            text = annotatedString,
            textAlign = textAlign,
            overflow = overflow,
            maxLines = maxLines,
            textDecoration = textDecoration,
            lineHeight = lineHeight
        )

//        Row {
//            val words = text.split(" ")
//            words.forEach { word ->
//                val firstLetter = word.firstOrNull()?.uppercase() ?: ""
//                val remainingText = word.drop(1).uppercase()
//                AppText(
//                    modifier = modifier,
//                    text = firstLetter,
//                    style = style.copy(fontSize = fontSizeCap),
//                    textAlign = textAlign,
//                    overflow = overflow,
//                    maxLines = maxLines,
//                    textDecoration = textDecoration,
//                    lineHeight = lineHeight
//                )
//                AppText(
//                    modifier = modifier,
//                    text = remainingText,
//                    style = style,
//                    textAlign = textAlign,
//                    overflow = overflow,
//                    maxLines = maxLines,
//                    textDecoration = textDecoration,
//                    lineHeight = lineHeight
//                )
//            }
//        }
    } else {
        AppText(
            modifier = modifier,
            text = text,
            style = style,
            textAlign = textAlign,
            overflow = overflow,
            maxLines = maxLines,
            textDecoration = textDecoration,
            lineHeight = lineHeight
        )
    }
}

@Composable
fun AppTextBody(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = textColor,
    fontSize: TextUnit = 14.sp,
    fontFamily: FontFamily = AppFontFamily(),
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = color,
        fontWeight = FontWeight.Light,
        fontSize = fontSize,
        fontFamily = fontFamily
    ),
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = 18.sp
) {
    AppText(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        textDecoration = textDecoration,
        lineHeight = lineHeight
    )
}

@Composable
fun AppTextLabel(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = textColor,
    fontSize: TextUnit = 14.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    fontFamily: FontFamily = AppFontFamily(),
    style: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        color = color,
        fontWeight = fontWeight,
        fontSize = fontSize,
        fontFamily = fontFamily
    ),
    textAlign: TextAlign = TextAlign.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    textDecoration: TextDecoration = TextDecoration.None,
    lineHeight: TextUnit = 18.sp
) {
    AppText(
        modifier = modifier,
        text = text,
        style = style,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        textDecoration = textDecoration,
        lineHeight = lineHeight
    )
}

@Composable
fun AppTextStyle(color: Color = textColor, fontSize: TextUnit = 14.sp) =
    MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize, color = color)

@Composable
fun AppTextSmall(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color
    )
}

@Composable
fun AppTextSmall(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = color
    )
}

fun TextStyle.toSpanStyle() =
    SpanStyle(
        color = color,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily
    )