package com.weberbox.pifire.application

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.analytics.Analytics
import com.weberbox.pifire.core.analytics.AnalyticsEvent
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.data.util.getAppLanguage
import com.weberbox.pifire.core.log.DebugLogTree
import com.weberbox.pifire.core.log.SentryIO
import com.weberbox.pifire.core.notification.createAlertChannels
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

        // Before any push can arrive: relay alerts post to these channels, OneSignal's name them.
        createAlertChannels(this)

        analytics.setUserProperty(
            name = AnalyticsEvent.Language.name,
            value = getAppLanguage(this),
        )

        analytics.setUserProperty(
            name = AnalyticsEvent.AppFlavor.name,
            value = BuildConfig.FLAVOR,
        )
    }

    companion object {
        private lateinit var app: App

        fun getApp(): App = app

        fun getAppContext(): Context = app.applicationContext
    }

    @Suppress("unused")
    private fun strictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder().detectAll().penaltyLog().build(),
            )
            StrictMode.setVmPolicy(VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }
}
