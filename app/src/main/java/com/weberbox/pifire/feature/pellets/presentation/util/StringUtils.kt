package com.weberbox.pifire.feature.pellets.presentation.util

import java.util.Locale

internal fun formatPercentage(percent: Int): String = String.format(Locale.US, "%s %s", percent, "%")
