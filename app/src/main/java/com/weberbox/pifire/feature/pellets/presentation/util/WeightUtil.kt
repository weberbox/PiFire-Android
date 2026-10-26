package com.weberbox.pifire.feature.pellets.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.weberbox.pifire.R

private const val POUNDS_PER_GRAM = 0.00220462
private const val OUNCES_PER_GRAM = 0.03527392

@Composable
internal fun gramsToImperial(grams: Double): String {
    val pounds = grams * POUNDS_PER_GRAM
    return if (pounds > 1) {
        stringResource(R.string.pellets_weight_lbs, pounds)
    } else {
        stringResource(R.string.pellets_weight_oz, grams * OUNCES_PER_GRAM)
    }
}

@Composable
internal fun gramsToMetric(grams: Double): String = if (grams < 1000) {
    stringResource(R.string.pellets_weight_g, grams)
} else {
    stringResource(R.string.pellets_weight_kg, grams / 1000)
}
