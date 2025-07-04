package com.weberbox.pifire.common.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.weberbox.pifire.changelog.presentation.screens.ChangelogScreenDestination
import com.weberbox.pifire.common.presentation.util.fadeEnterTransition
import com.weberbox.pifire.common.presentation.util.fadeExitTransition
import com.weberbox.pifire.common.presentation.util.scaleEnterTransition
import com.weberbox.pifire.common.presentation.util.sharedViewModel
import com.weberbox.pifire.common.presentation.util.slideEnterTransition
import com.weberbox.pifire.common.presentation.util.slidePopEnterTransition
import com.weberbox.pifire.home.presentation.screens.HomeScreenDestination
import com.weberbox.pifire.info.presentation.model.Licenses
import com.weberbox.pifire.info.presentation.screens.InfoScreenDestination
import com.weberbox.pifire.info.presentation.screens.LicenseDetailsScreen
import com.weberbox.pifire.info.presentation.screens.PipModulesDetailsScreen
import com.weberbox.pifire.landing.presentation.screens.HeaderSettingsDestination
import com.weberbox.pifire.landing.presentation.screens.LandingScreenDestination
import com.weberbox.pifire.landing.presentation.screens.ServerSettingsDestination
import com.weberbox.pifire.landing.presentation.screens.SettingsScreenDestination
import com.weberbox.pifire.pellets.presentation.screens.BrandsDetailsScreen
import com.weberbox.pifire.pellets.presentation.screens.LogsDetailsScreen
import com.weberbox.pifire.pellets.presentation.screens.ProfilesDetailsScreen
import com.weberbox.pifire.pellets.presentation.screens.WoodsDetailsScreen
import com.weberbox.pifire.recipes.presentation.screens.RecipeDetailsScreenDestination
import com.weberbox.pifire.recipes.presentation.screens.RecipeImagesScreenDestination
import com.weberbox.pifire.recipes.presentation.screens.RecipesScreenDestination
import com.weberbox.pifire.recipes.presentation.screens.RecipesViewModel
import com.weberbox.pifire.settings.presentation.screens.SettingsHomeDestination
import com.weberbox.pifire.settings.presentation.screens.admin.AdminSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.admin.ManualSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.app.AppSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.name.NameSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.AppriseSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.IftttSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.InfluxDbSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.MqttSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.NotificationSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.NotificationSettingsViewModel
import com.weberbox.pifire.settings.presentation.screens.notifications.PushSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.PushbulletSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.notifications.PushoverSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.pellets.PelletSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.probe.ProbeSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.pwm.PwmControlDestination
import com.weberbox.pifire.settings.presentation.screens.pwm.PwmSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.safety.SafetySettingsDestination
import com.weberbox.pifire.settings.presentation.screens.timer.SmartStartSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.timer.TimerSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.work.PidSettingsDestination
import com.weberbox.pifire.settings.presentation.screens.work.WorkSettingsDestination
import com.weberbox.pifire.setup.presentation.screens.SetupScreenDestination
import kotlin.reflect.typeOf

@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        enterTransition = { slideEnterTransition() },
        exitTransition = { fadeExitTransition() },
        popEnterTransition = { slidePopEnterTransition() },
        popExitTransition = { fadeExitTransition() },
        startDestination = NavGraph.LandingDest
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
            ServerSettingsDestination(navController)
        }
        composable<NavGraph.LandingDest.HeaderSettings> {
            HeaderSettingsDestination(navController)
        }
    }
}

fun NavGraphBuilder.homeNavGraph(navController: NavHostController) {
    navigation<NavGraph.HomeDest>(
        startDestination = NavGraph.HomeDest.DashHome
    ) {
        composable<NavGraph.HomeDest.DashHome> {
            HomeScreenDestination(navController)
        }
        composable<NavGraph.HomeDest.BrandsDetails>(
            enterTransition = { scaleEnterTransition() }
        ) {
            BrandsDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.WoodsDetails>(
            enterTransition = { scaleEnterTransition() }
        ) {
            WoodsDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.ProfilesDetails>(
            enterTransition = { scaleEnterTransition() }
        ) {
            ProfilesDetailsScreen(navController)
        }
        composable<NavGraph.HomeDest.LogsDetails>(
            enterTransition = { scaleEnterTransition() }
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
            val viewModel = it.sharedViewModel<RecipesViewModel>(navController)
            RecipesScreenDestination(navController, viewModel)
        }
        composable<NavGraph.RecipesDest.Details> {
            RecipeDetailsScreenDestination(navController)
        }
        composable<NavGraph.RecipesDest.Images>(
            enterTransition = { scaleEnterTransition() }
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
            enterTransition = { scaleEnterTransition() }
        ) {
            PipModulesDetailsScreen(navController)
        }
        composable<NavGraph.InfoDest.LicenseDetails>(
            typeMap = mapOf(typeOf<Licenses>() to parcelableType<Licenses>()),
            enterTransition = { scaleEnterTransition() }
        ) {
            val args = it.toRoute<NavGraph.InfoDest.LicenseDetails>()
            LicenseDetailsScreen(navController, args)
        }
    }
}

fun NavGraphBuilder.settingsNavGraph(
    navController: NavHostController
) {

    navigation<NavGraph.SettingsDest>(
        startDestination = NavGraph.SettingsDest.SettingsHome
    ) {
        composable<NavGraph.SettingsDest.SettingsHome> {
            SettingsHomeDestination(navController)
        }
        composable<NavGraph.SettingsDest.Admin> {
            AdminSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Manual> {
            ManualSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.App> {
            AppSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Probe> {
            ProbeSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Name> {
            NameSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Work> {
            WorkSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pid> {
            PidSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pwm> {
            PwmSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.PwmControl> {
            PwmControlDestination(navController)
        }
        composable<NavGraph.SettingsDest.Pellets> {
            PelletSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Timer> {
            TimerSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.SmartStart> {
            SmartStartSettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Safety> {
            SafetySettingsDestination(navController)
        }
        composable<NavGraph.SettingsDest.Notifications> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            NotificationSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Push> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Ifttt> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            IftttSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Pushover> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushoverSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Pushbullet> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            PushbulletSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.InfluxDb> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            InfluxDbSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Mqtt> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            MqttSettingsDestination(navController, viewModel)
        }
        composable<NavGraph.SettingsDest.Apprise> {
            val viewModel = it.sharedViewModel<NotificationSettingsViewModel>(navController)
            AppriseSettingsDestination(navController, viewModel)
        }
    }
}