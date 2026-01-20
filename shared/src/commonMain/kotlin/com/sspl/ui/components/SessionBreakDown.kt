package com.sspl.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sspl.core.models.Breakdown
import com.sspl.theme.columnColor
import com.sspl.utils.FORMAT_TIME_12HOUR
import com.sspl.utils.VERTICAL_DOTS
import com.sspl.utils.getJoinedDateTime

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 01/02/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun ColumnScope.SessionBreakDown(
    breakdown: List<Breakdown>,
    isClickEnabled: Boolean = true,
    onClick: (Breakdown) -> Unit = {}
) {
    breakdown.forEachIndexed { index, item ->
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable(enabled = isClickEnabled) { onClick.invoke(item) }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(.22F).defaultMinSize(minHeight = 68.dp)
            ) {
                val time = getJoinedDateTime(
                    item.startsAt ?: "",
                    item.endsAt ?: "",
                    outFormat = FORMAT_TIME_12HOUR,
                    divider = VERTICAL_DOTS,
                    separator = "\n"
                )
                AppTextLabel(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    text = time,
                    textAlign = TextAlign.Center
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(1F)
                    .padding(all = 8.dp)
            ) {
                AppTextLabel(
                    text = item.title.orEmpty().trim()
                )
                Spacer(modifier = Modifier.size(4.dp))
                val uniqueSpeakers = item.speakers
                    .map {
                        it.fullName()
                    } // Extract speaker names
                    .distinct() // Keep only unique speakers
                    .take(3) // Get the first 3 unique speakers
                val speakers = uniqueSpeakers.joinToString("\n").trim()
                AppTextBody(text = speakers)
                Spacer(modifier = Modifier.size(8.dp))
            }
        }
        if (breakdown.size != index + 1) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(.78F)
                    .padding(start = 12.dp)
                    .align(alignment = Alignment.End),
                thickness = 1.dp,
                color = columnColor
            )
        }
    }
}
