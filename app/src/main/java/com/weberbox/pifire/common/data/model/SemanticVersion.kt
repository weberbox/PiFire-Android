package com.weberbox.pifire.common.data.model

data class SemanticVersion(
    val major: Int,
    val minor: Int,
    val patch: Int
) : Comparable<SemanticVersion> {

    override fun compareTo(other: SemanticVersion): Int {
        return compareValuesBy(this, other,
            { it.major },
            { it.minor },
            { it.patch }
        )
    }

    companion object {
        // Parses by position so an unreadable part becomes 0 instead of shifting the others
        // ("v1.10.0" -> 1.10.0, "1.9.0-beta" -> 1.9.0).
        fun fromString(version: String): SemanticVersion {
            val parts = version.trim().removePrefix("v").removePrefix("V").split(".")
            fun part(index: Int): Int =
                parts.getOrNull(index)?.takeWhile { it.isDigit() }?.toIntOrNull() ?: 0
            return SemanticVersion(
                major = part(0),
                minor = part(1),
                patch = part(2),
            )
        }
    }
}