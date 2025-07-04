package com.weberbox.pifire.pellets.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PelletsDto(
    val uuid: String? = null,
    val pellets: Pellets? = null
) {
    @Serializable
    data class Pellets(
        @SerialName("archive")
        val profiles: Map<String, PelletProfile>? = null,

        @SerialName("current")
        val current: Current? = null,

        @SerialName("brands")
        val brands: List<String>? = null,

        @SerialName("lastupdated")
        val lastUpdated: LastUpdated? = null,

        @SerialName("log")
        val log: Map<String, String>? = null,

        @SerialName("woods")
        val woods: List<String>? = null,
    ) {
        @Serializable
        data class PelletProfile(
            @SerialName("brand")
            val brand: String? = null,

            @SerialName("wood")
            val wood: String? = null,

            @SerialName("rating")
            val rating: Int? = null,

            @SerialName("comments")
            val comments: String? = null,

            @SerialName("id")
            val id: String? = null

        )

        @Serializable
        data class LastUpdated(
            @SerialName("time")
            val time: Long? = null,
        )

        @Serializable
        data class Current(
            @SerialName("date_loaded")
            val dateLoaded: String? = null,

            @SerialName("est_usage")
            val estUsage: Double? = null,

            @SerialName("hopper_level")
            val hopperLevel: Int? = null,

            @SerialName("pelletid")
            val pelletId: String? = null
        )
    }
}