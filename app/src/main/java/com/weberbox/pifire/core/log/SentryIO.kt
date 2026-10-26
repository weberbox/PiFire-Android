package com.weberbox.pifire.core.log

import android.content.Context
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.model.settings.Modules
import com.weberbox.pifire.core.repo.SettingsRepo
import io.sentry.Hint
import io.sentry.IScope
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.SentryOptions
import io.sentry.android.core.SentryAndroid
import io.sentry.android.timber.SentryTimberIntegration
import io.sentry.protocol.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SentryIO @Inject constructor(
    private val appContext: Context,
    private val appScope: CoroutineScope,
    private val settingsRepo: SettingsRepo,
    private val prefs: Prefs,
) {
    // Read by beforeSend on whichever thread sends the event
    @Volatile
    private var server: Server? = null

    fun init() {
        // Synchronous so crashes during startup are reported too
        initSentry(appContext)
        observeCurrentServer()
    }

    private fun observeCurrentServer() {
        appScope.launch {
            settingsRepo.getCurrentServerFlow()
                .catch { e -> Timber.e(e, "Failed reading server data from DataStore") }
                .collect { server = it }
        }
    }

    private fun initSentry(appContext: Context) {
        if (sentryEnabled() && sentryDSNSet()) {
            SentryAndroid.init(appContext) { options ->
                options.sessionReplay.onErrorSampleRate = 1.0
                options.sessionReplay.sessionSampleRate = 0.1
                // Masked explicitly: screens show server addresses, which may hold credentials
                options.sessionReplay.setMaskAllText(true)
                options.sessionReplay.setMaskAllImages(true)
                options.screenshot.setMaskAllText(true)
                options.screenshot.setMaskAllImages(true)
                options.isEnableNdk = false
                options.isAttachScreenshot = true
                options.isEnableAutoSessionTracking = true
                options.isEnableDeduplication = true
                options.dsn = BuildConfig.SENTRY_DSN
                // One Sentry project for every flavor; the environment tells them apart.
                options.environment = "${BuildConfig.FLAVOR}-${BuildConfig.BUILD_TYPE}"
                options.tracesSampleRate = 1.0
                options.addIntegration(
                    SentryTimberIntegration(
                        minEventLevel = SentryLevel.ERROR,
                        minBreadcrumbLevel = SentryLevel.INFO,
                    ),
                )
                options.beforeSend =
                    SentryOptions.BeforeSendCallback { event: SentryEvent, _: Hint? ->
                        event.setExtra(
                            "PiFire Version",
                            server?.settings?.serverVersion,
                        )
                        event.setExtra(
                            "PiFire Build",
                            server?.settings?.serverBuild,
                        )
                        event.setExtra(
                            "Recipes Version",
                            server?.settings?.recipesVersion,
                        )
                        event.setExtra(
                            "Temp Units",
                            server?.settings?.tempUnits,
                        )
                        event.setExtra(
                            "RealHW",
                            server?.settings?.realHw,
                        )
                        event.setExtra(
                            "PWM",
                            server?.settings?.dcFan,
                        )
                        event.setExtra(
                            "Modules",
                            getServerModules(),
                        )
                        event.setExtra(
                            "Platform Type",
                            server?.settings?.platformType,
                        )
                        event.setExtra(
                            "Platform Current",
                            server?.settings?.platformCurrent,
                        )
                        event.setExtra(
                            "Controller Selected",
                            server?.settings?.cntrlrSelected,
                        )

                        event
                    }
            }

            Sentry.configureScope { scope: IScope ->
                scope.setTag("Application Name", appContext.getString(R.string.app_name))
                scope.setTag("Variant", BuildConfig.BUILD_TYPE)
                scope.setTag("Flavor Type", BuildConfig.FLAVOR)
                scope.setTag("Git Branch", BuildConfig.GIT_BRANCH)
                scope.setTag("Git Revision", BuildConfig.GIT_REV)
            }

            setUserEmail(prefs.get(Pref.sentryUserEmail))
        }
    }

    private fun setUserEmail(email: String) {
        if (email.isNotEmpty()) {
            User().apply {
                this.email = email
                Sentry.setUser(this)
            }
        }
    }

    private fun getServerModules(): Modules = Modules(
        grillplat = server?.settings?.modulesPlatform,
        display = server?.settings?.modulesDisplay,
        dist = server?.settings?.modulesDistance,
    )

    private fun sentryDSNSet(): Boolean = BuildConfig.SENTRY_DSN.isNotEmpty()

    private fun sentryEnabled(): Boolean = prefs.get(Pref.sentryEnabled)
}
