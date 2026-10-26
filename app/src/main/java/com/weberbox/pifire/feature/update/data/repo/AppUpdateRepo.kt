package com.weberbox.pifire.feature.update.data.repo

import com.weberbox.pifire.feature.update.data.model.AppUpdatePolicy
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import kotlinx.coroutines.flow.StateFlow

/** App updates: what Remote Config lists, and installing them through Google Play. */
interface AppUpdateRepo {
    val source: UpdateSource

    /** Always [InstallProgress.Idle] for [UpdateSource.GITHUB]. */
    val progress: StateFlow<InstallProgress>

    /** Null when Remote Config lists nothing newer than this version. */
    suspend fun fetchPolicy(): AppUpdatePolicy?

    /** Google Play: what Play offers this device. GitHub: the latest version in [policy]. */
    suspend fun findUpdate(policy: AppUpdatePolicy): AvailableUpdate?

    /** Google Play only; false when Play couldn't start it. */
    suspend fun startUpdate(immediate: Boolean): Boolean

    /** Restarts into a downloaded Google Play update. */
    fun completeUpdate()

    /** Whether [versionCode] was put off ("Later") and isn't due to be offered again yet. */
    fun isSnoozed(versionCode: Int): Boolean

    fun snooze(versionCode: Int)
}
