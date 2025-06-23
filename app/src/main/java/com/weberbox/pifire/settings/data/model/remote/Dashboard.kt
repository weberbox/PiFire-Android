package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dashboard(
    @SerialName("current")
    val current: String? = null,

    @SerialName("dashboards")
    val dashboards: Dashboards? = null
) {

    @Serializable
    data class Dashboards(
        @SerialName("Basic")
        var basic: Basic? = null,

        @SerialName("Default")
        var default: Default? = null
    ) {

        @Serializable
        data class Basic(
            @SerialName("config")
            var config: Config? = null,

            @SerialName("friendly_name")
            var friendlyName: String? = null,

            @SerialName("html_name")
            var htmlName: String? = null,

            @SerialName("metadata")
            var metadata: String? = null,

            @SerialName("name")
            var name: String? = null
        ) {

            @Serializable
            data class Config(
                @SerialName("max_food_temp_C")
                var maxFoodTempC: Int? = null,

                @SerialName("max_food_temp_F")
                var maxFoodTempF: Int? = null,

                @SerialName("max_primary_temp_C")
                var maxPrimaryTempC: Int? = null,

                @SerialName("max_primary_temp_F")
                var maxPrimaryTempF: Int? = null

            )
        }

        @Serializable
        data class Default(
            @SerialName("config")
            var config: Config? = null,

            @SerialName("friendly_name")
            var friendlyName: String? = null,

            @SerialName("html_name")
            var htmlName: String? = null,

            @SerialName("metadata")
            var metadata: String? = null,

            @SerialName("name")
            var name: String? = null
        ) {

            @Serializable
            data class Config(
                @SerialName("max_food_temp_C")
                var maxFoodTempC: Int? = null,

                @SerialName("max_food_temp_F")
                var maxFoodTempF: Int? = null,

                @SerialName("max_primary_temp_C")
                var maxPrimaryTempC: Int? = null,

                @SerialName("max_primary_temp_F")
                var maxPrimaryTempF: Int? = null

            )
        }
    }
}
