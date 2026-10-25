package com.weberbox.pifire.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.ui.util.fadeEnterTransition
import com.weberbox.pifire.core.ui.util.fadeExitTransition
import com.weberbox.pifire.core.ui.util.scaleEnterTransition
import com.weberbox.pifire.core.ui.util.slideEnterTransition
import com.weberbox.pifire.core.ui.util.slidePopEnterTransition
import com.weberbox.pifire.feature.changelog.presentation.screen.ChangelogScreenDestination
import com.weberbox.pifire.feature.home.presentation.screen.HomeScreenDestination
import com.weberbox.pifire.feature.info.data.model.Licenses
import com.weberbox.pifire.feature.info.presentation.screen.InfoScreenDestination
import com.weberbox.pifire.feature.info.presentation.screen.LicenseDetailsScreen
import com.weberbox.pifire.feature.info.presentation.screen.PipModulesDetailsScreen
import com.weberbox.pifire.feature.landing.presentation.screen.HeaderSettingsScreenDestination
import com.weberbox.pifire.feature.landing.presentation.screen.LandingScreenDestination
import com.weberbox.pifire.feature.landing.presentation.screen.ServerSettingsScreenDestination
import com.weberbox.pifire.feature.landing.presentation.screen.SettingsScreenDestination
import com.weberbox.pifire.feature.pellets.presentation.screen.BrandsDetailsScreen
import com.weberbox.pifire.feature.pellets.presentation.screen.LogsDetailsScreen
import com.weberbox.pifire.feature.pellets.presentation.screen.ProfilesDetailsScreen
import com.weberbox.pifire.feature.pellets.presentation.screen.WoodsDetailsScreen
import com.weberbox.pifire.feature.recipes.presentation.screen.RecipeDetailsScreenDestination
import com.weberbox.pifire.feature.recipes.presentation.screen.RecipeImagesScreenDestination
import com.weberbox.pifire.feature.recipes.presentation.screen.RecipesScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.SettingsHomeScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.admin.AdminSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.admin.ManualSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.app.AppSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.AppriseSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.IftttSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.InfluxDbSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.MqttSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.NotificationSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.NotificationSettingsViewModel
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.PushSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.PushbulletSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.PushoverSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.WLedProfilesScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.WLedSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.notifications.WLedSuggestedProfilesScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.pellets.PelletSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.probe.ProbeSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.pwm.PwmControlScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.pwm.PwmSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.safety.SafetySettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.timer.SmartStartSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.timer.TimerSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.ui.UISettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.work.PidSettingsScreenDestination
import com.weberbox.pifire.feature.settings.presentation.screen.work.WorkSettingsScreenDestination
import com.weberbox.pifire.feature.setup.presentation.screen.SetupScreenDestination
import kotlin.reflect.typeOf

@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController(),
    analytics: Analytics,
) {
    NavHost(
        navController = navController,
        enterTransition = { slideEnterTransition() },
        exitTransition = { fadeExitTransition() },
        popEnterTransition = { slidePopEnterTransition() },
        popExitTransition = { fadeExitTransition() },
        startDestination = NavGraph.LandingDest,
    ) {
        composable<NavGraph.Changelog> {
            ChangelogScreenDestination(navController)
        }
        composable<NavGraph.ServerSetup> {
            SetupScreenDestination(navController)
        }
        landingNavGraph(navController)
        homeNavGraph(navController)
        recipesNavGraph(navController)
        infoNavGraph(navController)
        settingsNavGraph(navController)
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            entry.destination.route?.let { route ->
                val screenName = route
                    .substringBefore("/")
                    .substringAfterLast('.')
                analytics.logEvent(
                    name = AnalyticsEvent.ScreenView.name,
                    params = mapOf(
                        AnalyticsEvent.Param.ScreenName.key to screenName,
                    ),
                )
            }
        }
    }
}

fun NavGraphBuilder.landingNavGraph(navController: NavHostController) {
    navigation<NavGraph.LandingDest>(
        startDestination = NavGraph.LandingDest.Landing(),
    ) {
        composable<NavGraph.LandingDest.Landing> {
            LandingScreenDestination(navController)
        }
        composable<NavGraph.LandingDest.Settings> {
            SettingsScreenDestination(navController)
        }
        composable<NavGraph.LandingDest.ServerSettings> {
            ServerSettingsScreenDestination(navController)
        }
        composable<NavGraph.LandingDest.HeaderSettings> {
            HeaderSettingsScreenDestination(navController)
        }
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation<NavGraph.HomeDest>(
        startDestination = NavGraph.HomeDest.DashHome,
    ) {
        composable<NavGraph.HomeDest.DashHome> { backStackEntry ->
            HomeScreenDestination(
                navController = navController,
                backStackEntry = backStackEntry,
            )
        }
        composable<NavGraph.HomeDest.BrandsDetails>(
            enterTransition = { scaleEnterTransition() },
        ) {
            BrandsDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.WoodsDetails>(
            enterTransition = { scaleEnterTransition() },
        ) {
            WoodsDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.ProfilesDetails>(
            enterTransition = { scaleEnterTransition() },
        ) {
            ProfilesDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.LogsDetails>(
            enterTransition = { scaleEnterTransition() },
        ) {
            LogsDetailsScreen(navController)
        }
    }
}

fun NavGraphBuilder.recipesNavGraph(navController: NavHostController) {
    navigation<NavGraph.RecipesDest>(
        startDestination = NavGraph.RecipesDest.Recipes,
    ) {
        composable<NavGraph.RecipesDest.Recipes> {
            RecipesScreenDestination(navController)
        }
        composable<NavGraph.RecipesDest.Details> {
            RecipeDetailsScreenDestination(navController)
        }
        composable<NavGraph.RecipesDest.Images>(
            enterTransition = { scaleEnterTransition() },
        ) {
            RecipeImagesScreenDestination(navController)
        }
    }
}

fun NavGraphBuilder.infoNavGraph(navController: NavHostController) {
    navigation<NavGraph.InfoDest>(
        startDestination = NavGraph.InfoDest.Info,
        popEnterTransition = { fadeEnterTransition() },
    ) {
        composable<NavGraph.InfoDest.Info> {
            InfoScreenDestination(navController)
        }
        composable<NavGraph.InfoDest.PipModulesDetails>(
            enterTransition = { scaleEnterTransition() },
        ) {
            PipModulesDetailsScreen(navController)
        }
        composable<NavGraph.InfoDest.LicenseDetails>(
            typeMap = mapOf(typeOf<Licenses>() to parcelableType<Licenses>()),
            enterTransition = { scaleEnterTransition() },
        ) {
            val args = it.toRoute<NavGraph.InfoDest.LicenseDetails>()
            LicenseDetailsScreen(navController, args)
        }
    }
}

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController,
) {
    navigation<NavGraph.SettingsDest>(
        startDestination = NavGraph.SettingsDest.SettingsHome,
    ) {
        composable<NavGraph.SettingsDest.SettingsHome> {
            SettingsHomeScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Admin> {
            AdminSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Manual> {
            ManualSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.App> {
            AppSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Probe> {
            ProbeSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.UI> {
            UISettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Work> {
            WorkSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pid> {
            PidSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pwm> {
            PwmSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.PwmControl> {
            PwmControlScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pellets> {
            PelletSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Timer> {
            TimerSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.SmartStart> {
            SmartStartSettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Safety> {
            SafetySettingsScreenDestination(navController)
        }
        composable<NavGraph.SettingsDest.Notifications> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            NotificationSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Push> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Ifttt> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            IftttSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Pushover> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushoverSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Pushbullet> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushbulletSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.InfluxDb> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            InfluxDbSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Mqtt> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            MqttSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Apprise> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            AppriseSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.WLED> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            WLedSettingsScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.WLEDProfiles> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            WLedProfilesScreenDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.WLEDSuggestedProfiles> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            WLedSuggestedProfilesScreenDestination(navController, viewModel)
        }
    }
}
