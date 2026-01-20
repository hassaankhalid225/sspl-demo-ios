package com.sspl.ui.conference.programs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.sspl.Screen
import com.sspl.core.ApiStates
import com.sspl.core.models.Conference
import com.sspl.core.models.ConferenceDetail
import com.sspl.core.models.Session
import com.sspl.theme.boxColor
import com.sspl.theme.columnColor
import com.sspl.theme.columnColorLight
import com.sspl.theme.orange
import com.sspl.ui.components.AppProgressBar
import com.sspl.ui.components.AppTextBody
import com.sspl.ui.components.AppTextLabel
import com.sspl.ui.components.AppTextSubTitle
import com.sspl.ui.components.ConferenceAddsOn
import com.sspl.ui.components.Error
import com.sspl.ui.components.MarkAttendanceButton
import com.sspl.ui.conference.AttendanceViewModel
import com.sspl.ui.conference.ConferenceDetailViewModel
import com.sspl.ui.userdetails.ProfileViewModel
import com.sspl.utils.FORMAT_DAY_MONTH
import com.sspl.utils.FORMAT_DEFAULT_DATE_TIME
import com.sspl.utils.FORMAT_TIME_12HOUR
import com.sspl.utils.VERTICAL_DOTS
import com.sspl.utils.getJoinedDateTime
import com.sspl.utils.isInRange
import com.sspl.utils.isNowAfter
import dev.jordond.compass.Coordinates
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.compose.viewmodel.koinViewModel

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 05/01/2025.
 * se.muhammadimran@gmail.com
 */

@Composable
fun ProgramDetailScreen(
    navController: NavController,
    viewModel: ConferenceDetailViewModel = koinViewModel(),
    attendanceViewModel: AttendanceViewModel = koinViewModel(),
    profileViewModel: ProfileViewModel = koinViewModel()
) {
    val data = navController.previousBackStackEntry?.savedStateHandle?.get<String>("data") ?: return
    val conference: Conference = Json.decodeFromString<Conference>(data)
    val scaffoldState = remember { SnackbarHostState() }
    val showLocationSuccessDialog by attendanceViewModel.showAttendanceDialogByLocation.collectAsStateWithLifecycle()
    val showImageSuccessDialog by attendanceViewModel.showAttendanceDialogByImage.collectAsStateWithLifecycle()

    val isGuestUser = profileViewModel.isGuestUser.collectAsStateWithLifecycle()

    if (showLocationSuccessDialog) {
        AttendanceDialog(
            title = "Attendance Marked",
            description = "Your attendance has been marked.",
            onDismiss = { attendanceViewModel.onDismissDialogs() })
    }
    if (showImageSuccessDialog) {
        AttendanceDialog(onDismiss = { attendanceViewModel.onDismissDialogs() })
    }

    LaunchedEffect(Unit) {
        viewModel.getConferenceById(conference.id)
    }

    LaunchedEffect(Unit) {
        attendanceViewModel.attendanceViaLocationStatus.collect { status ->
            when (status) {
                is ApiStates.Failure -> status.error.message?.let { scaffoldState.showSnackbar(it) }
                ApiStates.Loading -> scaffoldState.showSnackbar("Please wait...")
                is ApiStates.Success -> scaffoldState.showSnackbar("Attendance marked successfully")
                ApiStates.Idle -> {} // Do nothing
            }
        }
    }
    LaunchedEffect(Unit) {
        attendanceViewModel.attendanceViaImageStatus.collect { status ->
            when (status) {
                is ApiStates.Failure -> status.error.message?.let { scaffoldState.showSnackbar(it) }
                ApiStates.Loading -> scaffoldState.showSnackbar("Uploading image...")
                is ApiStates.Success -> scaffoldState.showSnackbar("Image uploaded for attendance approval!")
                ApiStates.Idle -> {} // Do nothing
            }
        }
    }
    val details by viewModel.conferences.collectAsStateWithLifecycle()
    when (details) {
        is ApiStates.Failure -> {
            Error(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                error = (details as ApiStates.Failure).error
            )
        }

        ApiStates.Loading -> {
            AppProgressBar()
        }

        ApiStates.Idle -> {
            // Do nothing, waiting for data
        }

        is ApiStates.Success -> {
            (details as ApiStates.Success<ConferenceDetail>).data?.let {
                Scaffold(
                    snackbarHost = { SnackbarHost(scaffoldState) },
                    containerColor = Color.Transparent
                ) { _ ->

                    ConferenceDetailContent(
                        navController = navController,
                        conference = conference,
                        conferenceDetail = it,
                        isGuestUser = isGuestUser,
                        onMarkAttendance = { session ->
                            attendanceViewModel.markAttendanceViaLocation(
                                conferenceId = conference.id.toLong(), sessionId = session
                            )
                        },
                        onMarkAttendanceByImage = { sessionID, image ->
                            attendanceViewModel.markAttendanceViaImage(
                                conferenceId = conference.id.toLong(), sessionId = sessionID, image
                            )
                        },
                        scaffoldState = scaffoldState
                    )
                }
            }
        }
    }
}

@Composable
private fun ConferenceDetailContent(
    navController: NavController,
    conference: Conference,
    conferenceDetail: ConferenceDetail,
    isGuestUser: State<Boolean?>,
    onMarkAttendance: (sessionId: Long) -> Unit = {},
    onMarkAttendanceByImage: (sessionId: Long, ByteArray) -> Unit,
    scaffoldState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(all = 12.dp)
    ) {
        item {
            ConferenceDetail(conference, conferenceDetail)
        }
        conferenceDetail.days?.let { listOfDays ->
            itemsIndexed(listOfDays, key = { i, _ -> i }) { i, item ->
                Column(modifier = Modifier.fillMaxWidth().let {
                    if (i == listOfDays.size - 1) {
                        it.clip(
                            shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp)
                        )
                    } else {
                        it
                    }
                }.background(boxColor)) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = columnColor
                    )
                    AppTextSubTitle(
                        modifier = Modifier.fillMaxWidth().background(color = columnColorLight)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        text = "Day 0${i + 1}",
                        color = orange
                    )
                    val sessionWithBreaks =
                        (item.breaks.orEmpty() + item.sessions.orEmpty()).sortedBy {
                            it.startsAt
                        }
                    if (sessionWithBreaks.isNotEmpty()) {
                        SessionsSchedule(
                            sessionWithBreaks,
                            isGuestUser,
                            onMarkAttendance = onMarkAttendance,
                            onMarkAttendanceByImage = onMarkAttendanceByImage,
                            onClick = { session ->
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "data", Json.encodeToString(session)
                                )
                                navController.navigate(route = Screen.SessionDetailScreen.route)
                            },
                            onFeedbackClicked = { session ->
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "conferenceID",
                                    conferenceDetail.id
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "sessionID",
                                    session.id
                                )
                                navController.navigate(route = Screen.FeedbackScreen.route)
                            },
                            scaffoldState = scaffoldState
                        )
                    }
                }

                if (i != listOfDays.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = columnColor
                    )
                }
            }
        }
    }
}


@Composable
private fun ConferenceDetail(
    conference: Conference, conferenceDetail: ConferenceDetail
) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(
            shape = RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp)
        ).background(columnColor).padding(all = 12.dp)
    ) {
        AppTextSubTitle(
            text = conference.title.orEmpty().trim()
        )
        Spacer(modifier = Modifier.size(4.dp))
        AppTextLabel(
            text = getJoinedDateTime(
                dateTime1 = conference.startDate.orEmpty(),
                dateTime2 = conference.endDate.orEmpty(),
                inFormat = FORMAT_DEFAULT_DATE_TIME,
                outFormat = FORMAT_DAY_MONTH
            ),
            color = MaterialTheme.colorScheme.tertiary,
        )

        Spacer(modifier = Modifier.size(8.dp))
        AppTextBody(
            text = conference.summary.orEmpty().trim()
        )
        if (!conferenceDetail.addOnSets.isNullOrEmpty()) {
            Spacer(modifier = Modifier.size(4.dp))
            ConferenceAddsOn(conferenceDetail.addOnSets)
        }
    }
}

@Composable
private fun ColumnScope.SessionsSchedule(
    sessions: List<Session>,
    isGuestUser: State<Boolean?>,
    onClick: (Session) -> Unit,
    onMarkAttendance: (sessionId: Long) -> Unit,
    onMarkAttendanceByImage: (sessionId: Long, ByteArray) -> Unit,
    onFeedbackClicked: (Session) -> Unit,
    scaffoldState: SnackbarHostState
) {
    if (sessions.isEmpty()) return
    sessions.forEachIndexed { index, session ->
        SessionItem(
            session, sessions.size != index + 1, isGuestUser, onClick, onMarkAttendance = {
                onMarkAttendance(
                    session.id
                )
            }, onMarkAttendanceByImage = {
                onMarkAttendanceByImage(session.id, it)
            },
            onFeedbackClicked = { onFeedbackClicked.invoke(session) },
            scaffoldState = scaffoldState
        )
    }
}

@Composable
private fun ColumnScope.SessionItem(
    session: Session,
    showDivider: Boolean,
    isGuestUser: State<Boolean?>,
    onClick: (Session) -> Unit,
    onMarkAttendance: () -> Unit,
    onMarkAttendanceByImage: (ByteArray) -> Unit,
    onFeedbackClicked: () -> Unit,
    scaffoldState: SnackbarHostState
) {
    Row(modifier = Modifier.fillMaxWidth().clickable(!session.isBreak) {
        onClick.invoke(session)
    }.padding(horizontal = 12.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(.22F).defaultMinSize(minHeight = 68.dp)
        ) {
            val startAt = session.startsAt
            val endAt = session.endsAt
            val time = getJoinedDateTime(
                startAt.orEmpty(),
                endAt.orEmpty(),
                outFormat = FORMAT_TIME_12HOUR,
                divider = VERTICAL_DOTS,
                separator = "\n"
            )
            AppTextLabel(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary,
                text = time,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(1F).defaultMinSize(minHeight = 68.dp)
                .padding(start = 8.dp, end = 8.dp, top = 8.dp)

        ) {
//            if (!session.addOnSets.isNullOrEmpty()) {
//                ConferenceAddsOn(session.addOnSets)
//                Spacer(modifier = Modifier.size(8.dp))
//            }
            AppTextSubTitle(
                text = session.title.trim()
            )
            Spacer(modifier = Modifier.size(4.dp))
            session.summary?.let { AppTextBody(text = session.summary.trim()) }
            Spacer(modifier = Modifier.size(8.dp))
            session.venue?.let { venue ->
                venue.title?.let {
                    AppTextBody(text = venue.title.trim(), color = orange)
                    Spacer(modifier = Modifier.size(8.dp))
                }
                if (!session.isBreak && isGuestUser.value == false) {
                    Row(
                        modifier = Modifier.align(alignment = Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val shouldShowFeedback = isNowAfter(
                            targetTime = session.endsAt,
                            graceMinutes = 24 * 60
                        )
                        val showAttendance = isInRange(
                            startDateTimeUtc = session.startsAt,
                            endDateTimeUtc = session.endsAt,
                            graceMinutes = 15
                        )
                        AnimatedVisibility(visible = shouldShowFeedback) {
                            AppTextLabel(
                                text = "Feedback",
                                textDecoration = TextDecoration.Underline,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable(
                                    onClick = {
                                        onFeedbackClicked.invoke()
                                    }
                                )
                            )
                        }
                        AnimatedVisibility(visible = showAttendance) {
                            MarkAttendanceButton(
                                modifier = Modifier,
                                scaffoldState = scaffoldState,
                                apiCoordinates = Coordinates(
                                    latitude = session.venue.lat ?: 0.0,
                                    longitude = session.venue.lng ?: 0.0
                                ),
                                onMarkAttendance = {
                                    onMarkAttendance(

                                    )
                                },
                                onMarkAttendanceByImage = {
                                    onMarkAttendanceByImage(it)
                                })
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

        }
    }
    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(.78F).padding(start = 12.dp)
                .align(alignment = Alignment.End), thickness = 1.dp, color = columnColor
        )
    }
}

@Composable
fun AttendanceDialog(
    modifier: Modifier = Modifier,
    description: String = "Your image has been uploaded for attendance approval.",
    title: String = "Image Uploaded", onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(title) },
        text = { Text(description) },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("OK")
            }
        })
}


