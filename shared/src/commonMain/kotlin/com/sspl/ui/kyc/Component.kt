package com.sspl.ui.kyc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspl.resources.Res
import com.sspl.resources.create_an_account_so_you_can_interact_seamlessly
import com.sspl.resources.create_new_account
import com.sspl.theme.primary
import com.sspl.ui.components.AppTextTitle
import com.sspl.utils.IconResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun HeaderSection(
    iconResource: IconResource? = null,
    title: String = stringResource(Res.string.create_new_account),
    subTitle: String = stringResource(Res.string.create_an_account_so_you_can_interact_seamlessly)
) {

    iconResource?.let {
        Image(
            modifier = Modifier
                .size(100.dp)
                .border(
                    shape = CircleShape,
                    border = BorderStroke(
                        width = 4.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.onPrimaryContainer,
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    )
                )
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                ).padding(all = 4.dp),
            painter = iconResource.icon(),
            contentDescription = null
        )
    }

    AppTextTitle(
        text = title, fontSize = 26.sp, color = primary
    )
    AppTextTitle(
        text = subTitle,
        textAlign = TextAlign.Center
    )
}
