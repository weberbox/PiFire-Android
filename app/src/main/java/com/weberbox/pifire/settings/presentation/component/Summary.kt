package com.weberbox.pifire.settings.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R

@Composable
internal fun getSummary(enabled: Boolean): String {
    return if (enabled) stringResource(R.string.enabled) else
        stringResource(R.string.disabled)
}

@Composable
internal fun getSummary(value: String): String {
    return value.ifBlank { stringResource(R.string.unset) }
}

@Composable
internal fun getTempSummary(value: String): String {
    return if (value == "F") stringResource(R.string.fahrenheit) else
        stringResource(R.string.celsius)
}

@Composable
internal fun getSummarySeconds(value: String): String {
    return stringResource(R.string.seconds_format, value)
}

@Composable
internal fun getSummaryMinutes(value: String): String {
    return stringResource(R.string.minutes_format, value)
}

@Composable
internal fun getSummaryGrams(value: String): String {
    return stringResource(R.string.grams_format, value)
}

@Composable
internal fun getSummaryPercent(value: String): String {
    return stringResource(R.string.percent_format, value)
}

@Composable
internal fun getSummaryTemp(value: String, units: String): String {
    return stringResource(R.string.temp_format, value, units)
}

@Composable
internal fun getSummaryHz(value: String): String {
    return stringResource(R.string.hz_format, value)
}

@Composable
internal fun getSummaryCm(value: String): String {
    return stringResource(R.string.cm_format, value)
}

@Composable
internal fun getSummaryGs(value: String): String {
    return stringResource(R.string.cm_format, value)
}