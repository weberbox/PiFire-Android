package com.weberbox.pifire.common.presentation.navigation

import com.weberbox.pifire.info.presentation.model.Licenses
import kotlinx.serialization.Serializable

@Serializable
sealed class NavGraph {

    @Serializable data object Changelog
    @Serializable data object ServerSetup

    @Serializable
    data object LandingDest {
        @Serializable data class Landing(val firstLaunch: Boolean = true)
        @Serializable data object Settings
        @Serializable data class ServerSettings(val uuid: String = "")
        @Serializable data class HeaderSettings(val uuid: String = "")
    }

    @Serializable
    data object HomeDest {
        @Serializable data object DashHome
        @Serializable data object Pellets
        @Serializable data object Dashboard
        @Serializable data object Events
        @Serializable data object BrandsDetails
        @Serializable data object WoodsDetails
        @Serializable data object ProfilesDetails
        @Serializable data object LogsDetails
    }

    @Serializable
    data object InfoDest {
        @Serializable data object Info
        @Serializable data object PipModulesDetails
        @Serializable data class LicenseDetails(val licenses: Licenses)
    }

    @Serializable
    data object SettingsDest {
        @Serializable data object SettingsHome
        @Serializable data object Admin
        @Serializable data object Manual
        @Serializable data object App
        @Serializable data object Probe
        @Serializable data object Name
        @Serializable data object Work
        @Serializable data object Pid
        @Serializable data object Pwm
        @Serializable data object PwmControl
        @Serializable data object Pellets
        @Serializable data object Timer
        @Serializable data object SmartStart
        @Serializable data object Safety
        @Serializable data object Notifications
        @Serializable data object Push
        @Serializable data object Ifttt
        @Serializable data object Mqtt
        @Serializable data object Pushover
        @Serializable data object Pushbullet
        @Serializable data object InfluxDb
        @Serializable data object Apprise
    }

    @Serializable
    data object  RecipesDest {
        @Serializable data object Recipes
        @Serializable data class Details(val filename: String, val step: Int)
        @Serializable data class Images(val filename: String, val index: Int)
    }
}