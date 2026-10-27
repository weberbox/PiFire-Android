package com.weberbox.pifire.feature.update.data.repo

import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepo
import com.weberbox.pifire.feature.update.data.installer.PlayAppInstaller
import com.weberbox.pifire.feature.update.data.mapper.toUpdatePolicy
import com.weberbox.pifire.feature.update.data.model.AppUpdatePolicy
import com.weberbox.pifire.feature.update.data.model.AvailableUpdate
import com.weberbox.pifire.feature.update.data.model.InstallProgress
import com.weberbox.pifire.feature.update.data.model.UpdateSource
import dagger.Lazy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

/** How long "Later" holds back the offer of one version. */
private val SNOOZE_DURATION = 1.days

class AppUpdateRepoImpl @Inject constructor(
    private val remoteConfigRepo: RemoteConfigRepo,
    private val prefs: Prefs,
    private val playInstaller: Lazy<PlayAppInstaller>,
) : AppUpdateRepo {
    override val source = if (AppConfig.IS_PLAY_BUILD) UpdateSource.PLAY_STORE else UpdateSource.GITHUB

    override val progress: StateFlow<InstallProgress> = when (source) {
        UpdateSource.PLAY_STORE -> playInstaller.get().progress
        UpdateSource.GITHUB -> MutableStateFlow(InstallProgress.Idle).asStateFlow()
    }

    override suspend fun fetchPolicy(): AppUpdatePolicy? = remoteConfigRepo.fetchAppUpdateConfig()
        ?.toUpdatePolicy(currentVersionCode = BuildConfig.VERSION_CODE, includeAlpha = BuildConfig.ALPHA_BUILD)

    override suspend fun findUpdate(policy: AppUpdatePolicy): AvailableUpdate? = when (source) {
        UpdateSource.PLAY_STORE -> playInstaller.get().findUpdate()
        UpdateSource.GITHUB -> AvailableUpdate(versionCode = policy.latestVersionCode)
    }

    override suspend fun startUpdate(immediate: Boolean): Boolean =
        source == UpdateSource.PLAY_STORE && playInstaller.get().startUpdate(immediate = immediate)

    override fun completeUpdate() {
        if (source == UpdateSource.PLAY_STORE) playInstaller.get().completeUpdate()
    }

    override fun isSnoozed(versionCode: Int): Boolean =
        prefs.get(Pref.updateSnoozedVersionCode) == versionCode &&
            System.currentTimeMillis() < prefs.get(Pref.updateSnoozedUntil)

    override fun snooze(versionCode: Int) {
        prefs.set(Pref.updateSnoozedVersionCode, versionCode)
        prefs.set(Pref.updateSnoozedUntil, System.currentTimeMillis() + SNOOZE_DURATION.inWholeMilliseconds)
    }
}
