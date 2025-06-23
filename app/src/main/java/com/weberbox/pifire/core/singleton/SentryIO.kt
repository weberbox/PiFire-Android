package com.weberbox.pifire.core.singleton

import android.content.Context
import androidx.datastore.core.IOException
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.settings.data.model.local.Pref
import com.weberbox.pifire.settings.data.model.remote.Modules
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
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
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class SentryIO @Inject constructor(
    private val appContext: Context,
    private val appScope: CoroutineScope,
    private val settingsRepo: SettingsRepo,
    private val prefs: Prefs
) {
    private var server: Server? = null

    fun init() {
        getServerData()
    }

    private fun getServerData() {
        try {
            appScope.launch {
                server = settingsRepo.getCurrentServer()
                initSentry(appContext)
            }
        } catch (e: IOException) {
            Timber.e(e, "Failed reading server data from DataStore")
        }
    }

    private fun initSentry(appContext: Context) {
        if (sentryEnabled() && sentryDSNSet(appContext)) {
            SentryAndroid.init(appContext) { options ->
                options.sessionReplay.onErrorSampleRate = 1.0
                options.sessionReplay.sessionSampleRate = 0.1
                options.isEnableNdk = false
                options.isAttachScreenshot = true
                options.isEnableAutoSessionTracking = true
                options.isEnableDeduplication = true
                options.dsn = appContext.getString(R.string.def_sentry_io_dsn)
                options.tracesSampleRate = 1.0
                options.addIntegration(
                    SentryTimberIntegration(
                        minEventLevel = SentryLevel.ERROR,
                        minBreadcrumbLevel = SentryLevel.INFO
                    )
                )
                options.beforeSend =
                    SentryOptions.BeforeSendCallback { event: SentryEvent, _: Hint? ->
                        event.setExtra(
                            "PiFire Version",
                            server?.settings?.serverVersion
                        )
                        event.setExtra(
                            "PiFire Build",
                            server?.settings?.serverBuild
                        )
                        event.setExtra(
                            "Recipes Version",
                            server?.settings?.recipesVersion
                        )
                        event.setExtra(
                            "Temp Units",
                            server?.settings?.tempUnits
                        )
                        event.setExtra(
                            "RealHW",
                            server?.settings?.realHw
                        )
                        event.setExtra(
                            "PWM",
                            server?.settings?.dcFan
                        )
                        event.setExtra(
                            "Modules",
                            getServerModules()
                        )
                        event.setExtra(
                            "Platform Type",
                            server?.settings?.platformType
                        )
                        event.setExtra(
                            "Platform Current",
                            server?.settings?.platformCurrent
                        )
                        event.setExtra(
                            "Controller Selected",
                            server?.settings?.cntrlrSelected
                        )

                        if (event.isCrashed && event.eventId != null) {
                            storeCrashEvent(event.eventId.toString())
                        }

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
            val user = User()
            user.email = email
            Sentry.setUser(user)
        }
    }

    private fun getServerModules(): ArrayList<Modules> {
        val arrayList = ArrayList<Modules>()
        arrayList.add(
            Modules(
                grillplat = server?.settings?.modulesPlatform,
                display = server?.settings?.modulesDisplay,
                dist = server?.settings?.modulesDistance
            )
        )
        return arrayList
    }

    private fun sentryDSNSet(context: Context): Boolean {
        return context.getString(R.string.def_sentry_io_dsn).isNotEmpty()
    }

    private fun sentryEnabled(): Boolean {
        return prefs.get(Pref.sentryEnabled)
    }

    private fun storeCrashEvent(eventId: String) {
        prefs.set(Pref.sentryCrashEvent, eventId)
    }
}