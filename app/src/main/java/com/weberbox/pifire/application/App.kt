package com.weberbox.pifire.application

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.common.data.interfaces.Analytics
import com.weberbox.pifire.common.data.util.getAppLanguage
import com.weberbox.pifire.common.domain.AnalyticsEvent
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.log.DebugLogTree
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SentryIO
import com.weberbox.pifire.settings.data.model.local.Pref
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var sentryIO: SentryIO
    @Inject
    lateinit var analytics: Analytics

    override fun onCreate() {
        super.onCreate()
        app = this

        if (!AppConfig.DEBUG) {
            sentryIO.init()
        } else {
            Timber.plant(DebugLogTree("PiFireTag"))
            if (prefs.get(Pref.sentryDebugEnabled)) {
                sentryIO.init()
            }
        }

        Timber.d("Startup - Application Start")

        analytics.setUserProperty(
            name = AnalyticsEvent.Language.name,
            value = getAppLanguage(this)
        )

        analytics.setUserProperty(
            name = AnalyticsEvent.AppFlavor.name,
            value = BuildConfig.FLAVOR
        )

    }

    companion object {
        private lateinit var app: App

        fun getApp(): App {
            return app
        }

        fun getAppContext(): Context {
            return app.applicationContext
        }

    }

    @Suppress("unused")
    private fun strictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder().detectAll().penaltyLog().build()
            )
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }
}