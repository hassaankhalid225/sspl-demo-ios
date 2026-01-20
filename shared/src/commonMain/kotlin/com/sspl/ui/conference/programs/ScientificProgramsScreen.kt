package com.sspl.ui.conference.programs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.core.models.Conference
import com.sspl.core.models.ConferenceType
import com.sspl.platform.formatDateTime
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.dividerColor
import com.sspl.theme.orange
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.Error
import com.sspl.ui.components.MarkAttendanceButton
import com.sspl.ui.conference.ConferenceViewModel
import com.sspl.utils.FORMAT_DAY_MONTH
import com.sspl.utils.FORMAT_DEFAULT_DATE_TIME
import com.sspl.utils.isInRange
import com.sspl.utils.isNowAfter
import dev.jordond.compass.Coordinates
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/01/2025.
 * se.muhammadimran@gmail.com
 */

private const val TAG = ">>>ScientificProgramsScreen"

@Composable
fun ScientificProgramsScreen(
    navController: NavController,
    viewModel: ConferenceViewModel = koinViewModel()
) {
    val uiState by viewModel.conferences.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        println("$TAG LaunchedEffect(Unit)")
        viewModel.getAllConferences(ConferenceType.PROGRAM)
    }
    when (uiState) {
        is ApiStates.Loading -> {
            AppProgressBar()
        }

        is ApiStates.Failure -> {
            Error(
                modifier = Modifier.fillMaxSize(),
                error = (uiState as ApiStates.Failure).error
            )
        }

        ApiStates.Idle -> {
            // Do nothing, waiting for data
        }

        is ApiStates.Success -> {
            (uiState as ApiStates.Success<List<Conference>>).data?.let {
                println("$TAG Data: $it")
                ConferenceList(
                    conferences = it,
                    navController = navController
                )
            }
        }
    }
}


@Composable
fun ConferenceList(
    conferences: List<Conference>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(all = 16.dp)
    ) {
        itemsIndexed(conferences, key = { i, _ -> i }) { i, item ->
            ConferenceItem(
                item = item,
                position = i,
                showDivider = i != conferences.size - 1,
                onClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "data",
                        Json.encodeToString(item)
                    )
                    navController.navigate(route = Screen.ScientificProgramDetailScreen.route)
                },
                onFeedbackClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "conferenceID",
                        item.id
                    )
                    navController.navigate(route = Screen.FeedbackScreen.route)
                },
                onRegisterClick = {
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "data",
                        Json.encodeToString(item)
                    )
                    navController.navigate(route = Screen.ConferenceRegistrationScreen.route)
                }
            )
        }
    }
}

@Composable
fun ConferenceItem(
    item: Conference,
    position: Int,
    showDivider: Boolean = true,
    onClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().let {
            if (position == 0) {
                it.then(
                    Modifier.clip(
                        shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                    )
                )
            } else if (!showDivider) {
                Modifier.clip(
                    shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp)
                )
            } else {
                Modifier.clip(
                    shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp)
                )
            }
        }.background(
            color = columnColor
        ).clickable {
            onClick.invoke()
        }) {
        Column(
            modifier = Modifier.fillMaxWidth(.22F)
        ) {
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(
                        "${
                            formatDateTime(
                                dateTime = item.startDate ?: "",
                                inFormat = FORMAT_DEFAULT_DATE_TIME,
                                outFormat = FORMAT_DAY_MONTH
                            ).uppercase()
                        }\n"
                    )
                }
                withStyle(
                    style = SpanStyle(
                        color = orange, fontSize = 18.sp, fontWeight = FontWeight.Bold
                    )
                ) {
                    append("⋮\n")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(
                        formatDateTime(
                            dateTime = item.endDate ?: "",
                            inFormat = FORMAT_DEFAULT_DATE_TIME,
                            outFormat = FORMAT_DAY_MONTH
                        ).uppercase()
                    )
                }
            }
            Text(
                text = annotatedString,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp, start = 8.dp, end = 8.dp)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(1F).clip(
                shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp)
            ).background(color = boxColor)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(all = 8.dp)
            ) {
                AppTextSubTitle(
                    text = item.title ?: ""
                )
                Spacer(modifier = Modifier.size(8.dp))

                AppTextBody(
                    text = item.summary ?: ""
                )
                Spacer(modifier = Modifier.size(16.dp))

                Row(
                    modifier = Modifier.padding(end = 8.dp).align(alignment = Alignment.End),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val shouldShowFeedback = isNowAfter(
                        targetTime = item.startDate
                    )
                    AnimatedVisibility(visible = shouldShowFeedback) {
                        AppTextLabel(
                            text = "FEEDBACK",
                            textDecoration = TextDecoration.Underline,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(
                                onClick = {
                                    onFeedbackClick.invoke()
                                }
                            )
                        )
                    }
                    AppTextLabel(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.SemiBold,
                        text = "REGISTER NOW",
                        modifier = Modifier.clickable {
                            onRegisterClick.invoke()
                        }
                    )
                    AppTextLabel(
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.SemiBold,
                        text = "MORE DETAILS",
                        modifier = Modifier.clickable {
                            onClick.invoke()
                        }
                    )
                }
            }
            if (showDivider) {
                Spacer(
                    modifier = Modifier.fillMaxWidth().height(2.dp).padding(end = 12.dp)
                        .background(color = dividerColor)
                )
            }
        }
    }
}