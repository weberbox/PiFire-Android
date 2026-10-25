package com.weberbox.pifire.feature.info.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R
import java.util.Locale

private const val BYTES_PER_MB = 1024.0 * 1024
private const val BYTES_PER_GB = BYTES_PER_MB * 1024

/** Blank when unknown, so the info row shows "Unknown". */
@Composable
internal fun formatBytes(bytes: Long?): String = bytes?.let {
    stringResource(R.string.info_ram_value, it / BYTES_PER_GB, it / BYTES_PER_MB)
}.orEmpty()

@Composable
internal fun formatPercent(value: Double?): String = value?.let {
    val number = when {
        it % 1.0 == 0.0 -> it.toInt().toString()
        else -> String.format(Locale.US, "%.2f", it).trimEnd('0').trimEnd('.')
    }
    stringResource(R.string.info_percent_value, number)
}.orEmpty()
