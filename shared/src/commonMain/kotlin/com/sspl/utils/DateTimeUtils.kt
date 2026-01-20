package com.sspl.utils

import com.sspl.platform.formatDateTime
import io.ktor.util.date.GMTDateParser.Companion.MINUTES
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.minutes

object DateTimeUtils {
    // KMP-compatible method to get current time in milliseconds
    fun timeInMilliNow() = Clock.System.now().toEpochMilliseconds()
}

/**
 * MMM, yyyy EEEE
 */
const val FORMAT_DEFAULT_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
const val FORMAT_DAY_MONTH = "dd MMM"
const val FORMAT_MONTH_YEAR_WEEKDAY = "MMM, yyyy EEEE"
const val FORMAT_TIME_24HOUR = "HH:mm"
const val FORMAT_TIME_12HOUR = "hh:mm a"
const val DASH = "\u2014"
const val VERTICAL_DOTS = "⋮"


/**
 * @param dateTime as 2025-02-03T12:12:28.000Z
 * and it will return 20th,1st,2nd,3rd
 */
fun getDayOfMonthSuffixed(dateTime: String): String {
    return try {
        val dayOfMonth = dateTime.split("T").first().takeLast(2)
        val day = dayOfMonth.toInt()
        dayOfMonth + getSuffix(day)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

/**
 * @param day as day of month
 * and it will return th,st,nd,rd
 */
fun getSuffix(day: Int): String {
    val suffix = when {
        day in 11..13 -> "th"
        day % 10 == 1 -> "st"
        day % 10 == 2 -> "nd"
        day % 10 == 3 -> "rd"
        else -> "th"
    }
    return suffix
}

/**
 * Join to dates or time with hyphen
 * e.g 8:00 AM - 9:00 AM
 */
fun getJoinedDateTime(
    dateTime1: String,
    dateTime2: String,
    inFormat: String = FORMAT_DEFAULT_DATE_TIME,
    outFormat: String = FORMAT_DAY_MONTH,
    divider: String = DASH,
    separator: String = ""
): String =
    "${
        formatDateTime(
            dateTime = dateTime1,
            inFormat = inFormat,
            outFormat = outFormat
        )
    }$separator $divider $separator ${
        formatDateTime(
            dateTime = dateTime2,
            inFormat = inFormat,
            outFormat = outFormat
        )
    }"

fun isNowAfter(
    targetTime: String?,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    graceMinutes: Int = 0
): Boolean {
    if (targetTime.isNullOrEmpty()) return false
    val nowInstant = Clock.System.now()
    val now = nowInstant.toLocalDateTime(timeZone)
    val endTime = nowInstant.plus(graceMinutes.minutes).toLocalDateTime(timeZone)

    val utcInstant = Instant.parse(targetTime) // Parse the UTC time
    val localTimeZone = TimeZone.of(timeZone.id) // Get the local timezone
    val targetedDateTime =
        utcInstant.toLocalDateTime(localTimeZone) // Convert to local time

    println(">>> targetTime In: $targetTime")
    println(">>> Now: $now")
    println(">>> Targeted Out: $targetedDateTime")

    return targetedDateTime <= now && now <= endTime
}

/**
 * @param startDateTimeUtc is start date time
 * @param endDateTimeUtc is end date time
 * @param timeZone local default timezone
 * @param graceMinutes Grace time in minutes added to end time
 */
fun isInRange(
    startDateTimeUtc: String?,
    endDateTimeUtc: String?,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    graceMinutes: Int = 0
): Boolean {
    if (startDateTimeUtc.isNullOrEmpty() || endDateTimeUtc.isNullOrEmpty()) return false
    val localTimeZone = TimeZone.of(timeZone.id)
    val now = dateTimeNow()
    val startLocalDateTime =
        Instant.parse(startDateTimeUtc).toLocalDateTime(localTimeZone)
    val endLocalDateTime =
        Instant.parse(endDateTimeUtc).plus(graceMinutes.minutes)
            .toLocalDateTime(localTimeZone)


    return startLocalDateTime <= now && now <= endLocalDateTime
}

fun dateTimeNow(timeZone: TimeZone = TimeZone.currentSystemDefault()) =
    Clock.System.now().toLocalDateTime(timeZone)
