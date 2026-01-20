package com.sspl.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.sspl.theme.AppSpanStyle

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 09/01/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun TermsAndConditions(modifier: Modifier = Modifier, navController: NavHostController) {
//    var checked by remember {
//        mutableStateOf(false)
//    }
    val termsAndConditions = "Terms & Conditions"
    val privacyPolicy = "Privacy Policy"
    val text = buildAnnotatedString {
        append("By creating an account you are agreeing to our ")
        withLink(
            LinkAnnotation.Url(
                url = "https://joebirch.co",
                styles = TextLinkStyles(
                    style = AppSpanStyle().copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                ),
            )
        ) {
            append(termsAndConditions)
        }
//        withStyle(
//            style = AppSpanStyle().copy(
//                color = MaterialTheme.extendedColors.customColor.color
//            )
//        ) {
//            pushStringAnnotation(termsAndConditions, termsAndConditions)
//            append(termsAndConditions)
//        }
        append(" and ")
//        withStyle(
//            style = AppSpanStyle().copy(
//                color = MaterialTheme.extendedColors.customColor.color
//            )
//        ) {
//            pushStringAnnotation(privacyPolicy, privacyPolicy)
//            append(privacyPolicy)
//        }
        withLink(
            LinkAnnotation.Url(
                url = "https://joebirch.co",
                styles = TextLinkStyles(
                    style = AppSpanStyle().copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                ),
            )
        ) {
            append(privacyPolicy)
        }

    }
    Spacer(modifier = Modifier.height(4.dp))
    Row(modifier = modifier) {
//        Checkbox(checked = checked, onCheckedChange = {
//            checked = it
//        })
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
//            onClick = { offset ->
//                text.getStringAnnotations(offset, offset)
//                    .firstOrNull()?.let { span ->
//                        println("Clicked offset=$offset, and Text: ${span.item}")
//                    }
//            }
        )
    }
}