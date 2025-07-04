package com.weberbox.pifire.dashboard.presentation.util

import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.dashboard.presentation.model.ProbeType
import kotlin.math.abs

internal fun getDefaultProbeTemp(probeType: ProbeType, units: String): Int {
    return when (probeType) {
        ProbeType.Primary -> {
            if (isFahrenheit(units)) AppConfig.DEFAULT_GRILL_TEMP_SET_F else
                AppConfig.DEFAULT_GRILL_TEMP_SET_C
        }

        ProbeType.Food -> {
            if (isFahrenheit(units)) AppConfig.DEFAULT_PROBE_TEMP_SET_F else
                AppConfig.DEFAULT_PROBE_TEMP_SET_C
        }

        ProbeType.Aux -> {
            if (isFahrenheit(units)) AppConfig.DEFAULT_PROBE_TEMP_SET_F else
                AppConfig.DEFAULT_PROBE_TEMP_SET_C
        }
    }
}

internal fun getProbeMinTemp(probeType: ProbeType, units: String): Int {
    return when (probeType) {
        ProbeType.Primary -> {
            if (isFahrenheit(units)) AppConfig.MIN_GRILL_TEMP_SET_F else
                AppConfig.MIN_GRILL_TEMP_SET_C
        }

        ProbeType.Food -> {
            if (isFahrenheit(units)) AppConfig.MIN_PROBE_TEMP_SET_F else
                AppConfig.MIN_PROBE_TEMP_SET_C
        }

        ProbeType.Aux -> {
            if (isFahrenheit(units)) AppConfig.MIN_PROBE_TEMP_SET_F else
                AppConfig.MIN_PROBE_TEMP_SET_C
        }
    }
}

internal fun getProbeTempRange(
    probeType: ProbeType,
    units: String,
    maxTemp: Int,
    increment: Boolean
): List<Int> {
    val minTemp = getProbeMinTemp(probeType, units)
    return (minTemp..maxTemp).filter {
        if (increment) it % 5 == 0 else true
    }.reversed().toList()
}

internal fun isFahrenheit(units: String): Boolean {
    return units.equals("F", ignoreCase = true)
}

internal fun findClosestTemp(list: List<Int>, target: Int): Int {
    if (list.isEmpty()) {
        return 0
    }

    var closest = list[0]
    var minDiff = abs(target - closest)

    for (number in list) {
        val diff = abs(target - number)
        if (diff < minDiff) {
            minDiff = diff
            closest = number
        }
    }
    return closest
}