package com.weberbox.pifire.feature.pellets.presentation.util

import java.util.Locale

internal fun formatPercentage(percent: Int): String {
    return String.format(Locale.US, "%s %s", percent, "%")
}