package com.weberbox.pifire.common.presentation.util

import android.text.format.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun getFormattedDate(dateInMilliseconds: String, dateFormat: String): String {
    return DateFormat.format(dateFormat, dateInMilliseconds.toLong()).toString()
}

fun formatDateTimeString(
    input: String,
    inputFormat: String,
    outputFormat: String? = null,
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val outputFormatter =
        if (outputFormat == null) {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
        } else {
            DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
        }

    return try {
        val dateTime = LocalDateTime.parse(input, inputFormatter)
        dateTime.format(outputFormatter).replace("/", "-")
    } catch (_: Exception) {
        input
    }
}

fun formatDateString(
    input: String,
    inputFormat: String,
    outputFormat: String? = null
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val outputFormatter =
        if (outputFormat == null) {
            DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
        } else {
            DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
        }

    return try {
        val date = LocalDate.parse(input, inputFormatter)
        date.format(outputFormatter).replace("/", "-")
    } catch (_: Exception) {
        input
    }
}

fun formatTimeString(
    input: String,
    inputFormat: String,
    outputFormat: String? = null
): String {

    val inputFormatter = DateTimeFormatter.ofPattern(inputFormat)
    val outputFormatter =
        if (outputFormat == null) {
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
        } else {
            DateTimeFormatter.ofPattern(outputFormat, Locale.getDefault())
        }

    return try {
        val time = LocalTime.parse(input, inputFormatter)
        time.format(outputFormatter).replace("/", "-")
    } catch (_: Exception) {
        input
    }
}

@Suppress("unused")
fun formatSeconds(totalSeconds: Int?): String? {
    if (totalSeconds != null) {
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.US, "%02d:%02d", minutes, seconds)
        }
    }
    return null
}

fun formatMinutes(totalMinutes: Int?): String {
    if (totalMinutes != null && totalMinutes > 0) {
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return String.format(Locale.US, "%d:%02d", hours, minutes)
    }
    return "--:--"
}
