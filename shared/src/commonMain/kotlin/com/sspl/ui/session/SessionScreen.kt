package com.sspl.ui.session

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sspl.core.models.AddOn
import com.sspl.core.models.AddOnSet
import com.sspl.core.models.Breakdown
import com.sspl.core.models.Session
import com.sspl.core.models.Speaker
import com.sspl.core.models.Venue
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.orange
import com.sspl.theme.primary
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.AppTextTitle
import com.sspl.utils.FORMAT_DAY_MONTH
import com.sspl.utils.FORMAT_DEFAULT_DATE_TIME
import com.sspl.utils.FORMAT_TIME_12HOUR
import com.sspl.utils.getJoinedDateTime

@Deprecated("Not using this")
@Composable
fun SessionScreen(navController: NavController) {

    //val data = navController.previousBackStackEntry?.savedStateHandle?.get<String>("data") ?: return
    val session: Session = /*Json.decodeFromString<Session>(data)*/dummySession

    Column(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.Top)
            .padding(start = 12.dp, end = 12.dp, top = 12.dp).clip(
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
            ).background(boxColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().background(columnColor).clip(
                shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
            ).padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 6.dp)
        ) {
            AppTextSubTitle(
                text = session.title
            )
            Spacer(modifier = Modifier.size(4.dp))
            AppTextLabel(
                text = getJoinedDateTime(
                    dateTime1 = session.startsAt.orEmpty(),
                    dateTime2 = session.endsAt.orEmpty(),
                    inFormat = FORMAT_DEFAULT_DATE_TIME,
                    outFormat = FORMAT_DAY_MONTH
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            AppTextBody(
                text = session.summary.orEmpty()
            )
            // ConferenceAddOns(session.addOnSets)
        }
        Column {
            session.addOnSets?.let { adOns ->
                Column {
                    AppTextTitle("Ad-ons", modifier = Modifier.padding(start = 10.dp, top = 10.dp))
                    Spacer(Modifier.height(2.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(adOns) { index, adOn ->

                            AddOnSetItem(
                                adOn, index
                            )

                        }
                    }
                }
            }
            HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 6.dp), color = columnColor)

            session.breakdown?.let { breakdown ->
                Column {
                    AppTextTitle(
                        "Breakdown",
                        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(breakdown) { index, breakdownItem ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {

                                Timeline(index = index, lastIndex = breakdown.lastIndex)
                                // Breakdown Item
                                EachBreak(breakdown = breakdownItem, index)
                            }
                        }
                    }
                }
            }


        }


    }


}


@Composable
fun Timeline(modifier: Modifier = Modifier, index: Int, lastIndex: Int) {
    // Timeline Indicator
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Draw Solid Dot
        Canvas(modifier = Modifier.size(10.dp)) {
            drawCircle(color = primary, radius = size.minDimension / 2)
        }

        // Draw Dotted Line (only if it's not the last item)
        if (index <= lastIndex) {
            Canvas(
                modifier = Modifier
                    .width(2.dp)
                    .height(50.dp) // Adjust height based on item spacing
            ) {
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)
                drawLine(
                    color = primary.copy(alpha = 0.5f),
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = 4f,
                    pathEffect = pathEffect
                )
            }
        }
    }
}

@Composable
fun EachBreak(breakdown: Breakdown, index: Int) {

    Column(Modifier.padding(horizontal = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextSubTitle(
                text = "${index + 1}.", color = orange
            )
            Spacer(Modifier.width(3.dp))
            AppTextSubTitle(
                text = breakdown.title.orEmpty(), color = orange
            )
        }
        Spacer(Modifier.height(4.dp))

        AppTextLabel(
            text = getJoinedDateTime(
                dateTime1 = breakdown.startsAt.orEmpty(),
                dateTime2 = breakdown.endsAt.orEmpty(),
                inFormat = FORMAT_DEFAULT_DATE_TIME,
                outFormat = FORMAT_TIME_12HOUR
            )
        )

        Spacer(Modifier.height(2.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextLabel("Speakers :")
            Spacer(Modifier.width(4.dp))
            val speakers = breakdown.speakers.joinToString(",") { it.fullName() }.trim()
            AppTextBody(speakers)

        }
    }
}

@Composable
fun AddOnSetItem(addOnSet: AddOnSet, index: Int) {
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextSubTitle(
                text = "${index + 1}.", color = orange
            )
            Spacer(Modifier.width(3.dp))
            AppTextSubTitle(
                text = addOnSet.title.orEmpty(), color = orange
            )
        }

        addOnSet.addOns?.forEach { addOn ->
            Spacer(modifier = Modifier.size(4.dp))

            AddOnItem(addOn)
        }
    }
}

@Composable
fun AddOnItem(addOn: AddOn) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.Start) {
            AppTextLabel(text = addOn.title ?: "")
            Spacer(Modifier.height(2.dp))
            addOn.subtitle?.let { subtitle ->
                AppTextBody(text = subtitle)
            }
        }
    }
}

val dummySession = Session(
    id = 1234L,
    title = "Jetpack Compose: Building Modern Android UIs",
    summary = "Explore the power of Jetpack Compose in creating beautiful and responsive Android user interfaces. Learn best practices and advanced techniques.",
    venueId = 5678,
    webLink = "https://example.com/jetpack-compose-session",
    isBreak = false,

    startsAt = "2025-01-31T11:00:00.000Z",
    endsAt = "2025-01-31T11:00:00.000Z",
    addOnSets = listOf(
        AddOnSet(
            title = "Session Materials", addOns = listOf(
                AddOn(
                    title = "Printed Handouts", subtitle = "Detailed session notes and code samples"
                ), AddOn(
                    title = "Digital Resources",
                    subtitle = "Downloadable slides and example projects"
                )
            )
        ), AddOnSet(
            title = "Extended Learning", addOns = listOf(
                AddOn(
                    title = "Post-Session Q&A", subtitle = "30-minute exclusive Q&A with speakers"
                ), AddOn(
                    title = "Recording Access", subtitle = "1-year access to session recording"
                )
            )
        )
    ),
    breakdown = listOf(
        Breakdown(
            title = "Introduction to Jetpack Compose",
            startsAt = "2025-01-31T11:00:00.000Z",
            endsAt = "2025-01-31T11:00:00.000Z",
            isBreak = false,
            speakers = listOf(
                Speaker(
                    id = 101L,
                    email = "john.doe@example.com",
                    prefix = "Mr.",
                    firstName = "John",
                    lastName = "Doe",
                    gender = "Male"
                )
            )
        ), Breakdown(
            title = "Advanced Compose Techniques",
            startsAt = "2025-01-31T11:00:00.000Z",
            endsAt = "2025-01-31T11:00:00.000Z",
            isBreak = false,
            speakers = listOf(
                Speaker(
                    id = 102L,
                    email = "jane.smith@example.com",
                    prefix = "Ms.",
                    firstName = "Jane",
                    lastName = "Smith",
                    gender = "Female"
                )
            )
        ), Breakdown(
            title = "Q&A Session",
            startsAt = "2025-01-31T11:00:00.000Z",
            endsAt = "2025-01-31T11:00:00.000Z",
            isBreak = false,
            speakers = listOf(
                Speaker(
                    id = 101L,
                    email = "john.doe@example.com",
                    prefix = "Mr.",
                    firstName = "John",
                    lastName = "Doe",
                    gender = "Male"
                ), Speaker(
                    id = 102L,
                    email = "jane.smith@example.com",
                    prefix = "Ms.",
                    firstName = "Jane",
                    lastName = "Smith",
                    gender = "Female"
                )
            )
        )
    ),
    venue = Venue(
        id = 5678,
        title = "Tech Conference Center",
        address = "123 Innovation Street, San Francisco, CA 94122",
        lat = 37.7749,
        lng = -122.4194,
        radius = 3
    )

)
