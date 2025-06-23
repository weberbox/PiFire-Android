package com.weberbox.pifire.application

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.singleton.Prefs
import com.weberbox.pifire.core.singleton.SentryIO
import com.weberbox.pifire.core.util.DebugLogTree
import com.weberbox.pifire.settings.data.model.local.Pref
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject lateinit var prefs: Prefs
    @Inject lateinit var sentryIO: SentryIO

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