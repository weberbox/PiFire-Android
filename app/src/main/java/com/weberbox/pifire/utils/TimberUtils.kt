package com.weberbox.pifire.utils

import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.utils.log.DebugLogTree
import timber.log.Timber

object TimberUtils {

    @JvmStatic
    fun configTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugLogTree())
        }
    }
}