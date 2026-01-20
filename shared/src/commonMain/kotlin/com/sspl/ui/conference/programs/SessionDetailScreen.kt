package com.sspl.ui.conference.programs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.core.models.Breakdown
import com.sspl.core.models.Session
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.orange
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.ConferenceAddsOn
import com.sspl.ui.components.SessionBreakDown
import com.sspl.utils.DASH
import com.sspl.utils.FORMAT_TIME_12HOUR
import com.sspl.utils.getJoinedDateTime
import kotlinx.serialization.json.Json

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/01/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun SessionDetailScreen(
    navController: NavController
) {
    val data = navController.previousBackStackEntry?.savedStateHandle?.get<String>("data")
    if (data == null) return
    val session: Session = Json.decodeFromString<Session>(data)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 12.dp)
    ) {
        item {
            HeaderDetail(session)
        }
        item {
            Breakdown(session.breakdown)
        }
    }
}

@Composable
private fun HeaderDetail(
    session: Session
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
            )
            .background(columnColor)
            .padding(all = 12.dp)
    ) {
        AppTextSubTitle(
            text = session.title.trim()
        )

        if (!session.summary.isNullOrEmpty()) {
            AppTextBody(
                text = session.summary.orEmpty().trim()
            )
        }
        Spacer(modifier = Modifier.size(4.dp))

        val startAt = session.startsAt
        val endAt = session.endsAt
        val time = getJoinedDateTime(
            startAt.orEmpty(),
            endAt.orEmpty(),
            outFormat = FORMAT_TIME_12HOUR,
            divider = DASH
        )
        AppTextLabel(
            text = time,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Spacer(modifier = Modifier.size(8.dp))
        session.venue?.let { venue ->
            venue.title?.let {
                AppTextBody(text = venue.title.trim())
            }
            venue.address?.let {
                AppTextBody(text = venue.address.trim())
            }
            Spacer(modifier = Modifier.size(8.dp))
        }
        if (!session.addOnSets.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(4.dp))
            ConferenceAddsOn(session.addOnSets)
        }
    }
}

@Composable
private fun Breakdown(breakdown: List<Breakdown>?) {
    if (breakdown.isNullOrEmpty()) return
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
            )
            .background(boxColor)
            .padding(all = 12.dp)
    ) {
        AppTextSubTitle(
            text = "Breakdown",
            color = orange
        )
        SessionBreakDown(breakdown = breakdown, isClickEnabled = false)
    }
}