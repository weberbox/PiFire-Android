package com.weberbox.pifire.pellets.presentation.util

import java.util.Locale

internal fun gramsToImperial(grams: Double): String {
    val pounds = grams * 0.00220462
    val ounces = grams * 0.03527392
    return if (pounds > 1) {
        String.format(Locale.US, "%.2f lbs", pounds)
    } else {
        String.format(Locale.US, "%.2f oz", ounces)
    }
}

internal fun gramsToMetric(grams: Double): String {
    return if (grams < 1000) {
        String.format(Locale.US, "%.2f g", grams)
    } else {
        String.format(Locale.US, "%.2f kg", grams / 1000)
    }
}