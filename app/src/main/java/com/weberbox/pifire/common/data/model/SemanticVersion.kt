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
        fun fromString(version: String): SemanticVersion {
            val parts = version.split(".").mapNotNull { it.toIntOrNull() }
            return SemanticVersion(
                parts.getOrElse(0) { 0 },
                parts.getOrElse(1) { 0 },
                parts.getOrElse(2) { 0 }
            )
        }
    }
}