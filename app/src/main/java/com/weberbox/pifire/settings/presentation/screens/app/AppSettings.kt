package com.weberbox.pifire.settings.presentation.screens.app

import android.content.res.Configuration
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.base.SIDE_EFFECTS_KEY
import com.weberbox.pifire.common.presentation.component.InitialLoadingProgress
import com.weberbox.pifire.common.presentation.component.SettingsAppBar
import com.weberbox.pifire.common.presentation.model.AppTheme
import com.weberbox.pifire.common.presentation.screens.DataError
import com.weberbox.pifire.common.presentation.sheets.BottomSheet
import com.weberbox.pifire.common.presentation.sheets.InputValidationSheet
import com.weberbox.pifire.common.presentation.sheets.ValidationOptions
import com.weberbox.pifire.common.presentation.state.rememberCustomModalBottomSheetState
import com.weberbox.pifire.common.presentation.theme.PiFireTheme
import com.weberbox.pifire.common.presentation.util.safeNavigate
import com.weberbox.pifire.common.presentation.util.showAlerter
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.presentation.contract.AppContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import me.zhanghai.compose.preference.ListPreference
import me.zhanghai.compose.preference.ListPreferenceType
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceLocals
import me.zhanghai.compose.preference.listPreference
import me.zhanghai.compose.preference.preferenceCategory
import me.zhanghai.compose.preference.switchPreference

@Composable
fun AppSettingsDestination(
    navController: NavHostController,
    viewModel: AppSettingsViewModel = hiltViewModel()
) {
    ProvidePreferenceLocals {
        AppSettings(
            state = viewModel.viewState.value,
            effectFlow = viewModel.effect,
            onEventSent = { event -> viewModel.setEvent(event) },
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is AppContract.Effect.Navigation.Back -> navController.popBackStack()
                    is AppContract.Effect.Navigation.NavRoute -> {
                        navController.safeNavigate(
                            route = navigationEffect.route,
                            popUp = navigationEffect.popUp
                        )
                    }
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AppSettings(
    state: AppContract.State,
    effectFlow: Flow<AppContract.Effect>?,
    onEventSent: (event: AppContract.Event) -> Unit,
    onNavigationRequested: (AppContract.Effect.Navigation) -> Unit
) {
    val activity = LocalActivity.current
    val windowInsets = WindowInsets.safeDrawing
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when (effect) {
                is AppContract.Effect.Navigation -> {
                    onNavigationRequested(effect)
                }

                is AppContract.Effect.Notification -> {
                    activity?.showAlerter(
                        message = effect.text,
                        isError = effect.error
                    )
                }
            }
        }?.collect()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SettingsAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings_app_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior,
                onNavigate = { onNavigationRequested(AppContract.Effect.Navigation.Back) }
            )
        },
        containerColor = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = windowInsets,
    ) { contentPadding ->
        AnimatedContent(
            targetState = state,
            contentKey = { it.isInitialLoading or it.isDataError }
        ) { state ->
            when {
                state.isInitialLoading -> InitialLoadingProgress()
                state.isDataError -> DataError {
                    onNavigationRequested(AppContract.Effect.Navigation.Back)
                }
                else -> {
                    AppContent(
                        state = state,
                        onEventSent = onEventSent,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun AppContent(
    state: AppContract.State,
    onEventSent: (event: AppContract.Event) -> Unit,
    contentPadding: PaddingValues,
) {
    val emailSheet = rememberCustomModalBottomSheetState()
    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize(),
    ) {
        preferenceCategory(
            key = "settings_cat_ui",
            title = { Text(text = stringResource(R.string.settings_cat_app_ui)) }
        )
        item {
            ListPreference(
                value = state.appTheme,
                values = AppTheme.entries,
                title = { Text(text = stringResource(R.string.settings_app_theme_title)) },
                summary = { Text(text = stringResource(state.appTheme.title)) },
                onValueChange = { onEventSent(AppContract.Event.UpdateAppTheme(it)) },
                type = ListPreferenceType.DROPDOWN_MENU,
            )
        }
        switchPreference(
            key = Pref.dynamicColor.key,
            enabled = { Build.VERSION.SDK_INT >= Build.VERSION_CODES.S },
            defaultValue = Pref.dynamicColor.defaultValue,
            title = { Text(text = stringResource(R.string.settings_app_dynamic_color_title)) },
            summary = {
                Text(
                    text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                        stringResource(R.string.settings_app_dynamic_color_summary)
                    else
                        stringResource(R.string.settings_app_dynamic_color_disabled)
                )
            }
        )
        switchPreference(
            key = Pref.keepScreenOn.key,
            defaultValue = Pref.keepScreenOn.defaultValue,
            title = { Text(text = stringResource(R.string.settings_app_screen_on_title)) },
            summary = { Text(text = stringResource(R.string.settings_app_screen_on_summary)) }
        )
        switchPreference(
            key = Pref.showBottomBar.key,
            defaultValue = Pref.showBottomBar.defaultValue,
            title = { Text(text = stringResource(R.string.settings_app_bottom_bar_title))},
            summary = { Text(text = stringResource(R.string.settings_app_bottom_bar_summary)) }
        )
        preferenceCategory(
            key = "settings_cat_events",
            title = { Text(text = stringResource(R.string.settings_cat_events_title)) }
        )
        listPreference(
            key = Pref.eventsAmount.key,
            defaultValue = Pref.eventsAmount.defaultValue,
            values = listOf(10, 20, 30, 40, 50, 60),
            title = { Text(text = stringResource(R.string.settings_app_events_amount_title)) },
            summary = { Text(text = "$it ${stringResource(R.string.items)}") },
            type = ListPreferenceType.DROPDOWN_MENU,
        )
        preferenceCategory(
            key = "settings_cat_temps",
            title = { Text(text = stringResource(R.string.settings_cat_app_temps)) }
        )
        switchPreference(
            key = Pref.incrementTemps.key,
            defaultValue = Pref.incrementTemps.defaultValue,
            title = { Text(text = stringResource(R.string.settings_app_increment_temps)) },
            summary = { Text(text = stringResource(R.string.settings_app_increment_temps_summary)) }
        )
        preferenceCategory(
            key = "settings_cat_crash",
            title = { Text(text = stringResource(R.string.settings_cat_crash)) }
        )
        switchPreference(
            key = Pref.sentryEnabled.key,
            defaultValue = Pref.sentryEnabled.defaultValue,
            title = { Text(text = stringResource(R.string.settings_enable_crash)) },
            summary = { Text(text = stringResource(R.string.settings_crash_summary)) }
        )
        if (AppConfig.DEBUG) {
            switchPreference(
                key = Pref.sentryDebugEnabled.key,
                defaultValue = Pref.sentryDebugEnabled.defaultValue,
                title = { Text(text = stringResource(R.string.settings_dev_crash_title)) },
                summary = { Text(text = stringResource(R.string.settings_dev_crash_summary)) }
            )
        }
        item {
            Preference(
                title = { Text(text = stringResource(R.string.settings_crash_email)) },
                summary = { Text(text = stringResource(R.string.settings_crash_email_summary)) }
            ) {
                emailSheet.open()
            }
        }
    }
    BottomSheet(
        sheetState = emailSheet.sheetState
    ) {
        InputValidationSheet(
            input = state.userEmail,
            title = stringResource(R.string.settings_crash_email),
            placeholder = stringResource(R.string.settings_crash_email),
            leadingIcon = Icons.Outlined.Email,
            contentType = ContentType.EmailAddress,
            validationOptions = ValidationOptions(
                allowBlank = true
            ),
            onUpdate = {
                onEventSent(AppContract.Event.UpdateUserEmail(it))
                emailSheet.close()
            }
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
private fun AppSettingsPreview() {
    PiFireTheme {
        ProvidePreferenceLocals {
            Surface {
                AppSettings(
                    state = AppContract.State(
                        appTheme = AppTheme.System,
                        userEmail = "",
                        isInitialLoading = false,
                        isDataError = false
                    ),
                    effectFlow = null,
                    onEventSent = {},
                    onNavigationRequested = {}
                )
            }
        }
    }
}