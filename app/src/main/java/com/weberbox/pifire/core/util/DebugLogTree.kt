package com.weberbox.pifire.core.util

import android.os.Build
import android.util.Log
import timber.log.Timber

class DebugLogTree(private val prefix: String) : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Workaround for devices that do not show lower priority logs
        var updatedPriority = priority
        if (Build.MANUFACTURER == "HUAWEI" || Build.MANUFACTURER == "samsung") {
            if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
                updatedPriority = Log.ERROR
            }
        }
        super.log(updatedPriority, "$prefix.$tag", message, t)
    }

    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "(%s:%s) - %s",
            element.fileName,
            element.lineNumber,
            element.methodName
        )
    }
}