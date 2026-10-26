package com.weberbox.pifire.feature.settings.presentation.util

/**
 * Sorts a PiFire range table by temp. `profiles[i]` applies below `temps[i]` and the extra profile
 * past the last temp applies above it, so profiles move with their temps and the extra stays last.
 */
internal fun <P> sortRangesByTemp(temps: List<Int>, profiles: List<P>): Pair<List<Int>, List<P>> {
    val ranges = temps.zip(profiles).sortedBy { (temp, _) -> temp }
    return ranges.map { it.first } to ranges.map { it.second } + profiles.drop(ranges.size)
}

/**
 * Adds (index -1) or replaces a range in a table listed as one row per range plus the top row.
 * Adding to an empty table creates the first range with [profile] on both sides of [temp].
 */
internal fun <P> updateRange(
    index: Int,
    temp: Int,
    profile: P,
    rowTemps: List<Int>,
    rowProfiles: List<P>,
): Pair<List<Int>, List<P>> {
    val temps = rowTemps.toMutableList()
    val profiles = rowProfiles.toMutableList()
    when {
        index != -1 -> {
            temps[index] = temp
            temps.removeAt(temps.lastIndex)
            profiles[index] = profile
        }

        temps.isEmpty() -> {
            temps.add(temp)
            profiles.add(profile)
            profiles.add(profile)
        }

        else -> {
            temps[temps.lastIndex] = temp
            profiles.add(profile)
        }
    }
    return sortRangesByTemp(temps = temps, profiles = profiles)
}
