package com.weberbox.pifire.core.di

import com.weberbox.pifire.changelog.data.repo.ChangelogRepo
import com.weberbox.pifire.changelog.data.repo.ChangelogRepoImpl
import com.weberbox.pifire.dashboard.data.repo.DashRepo
import com.weberbox.pifire.dashboard.data.repo.DashRepoImpl
import com.weberbox.pifire.events.data.repo.EventsRepo
import com.weberbox.pifire.events.data.repo.EventsRepoImpl
import com.weberbox.pifire.info.data.repo.InfoRepo
import com.weberbox.pifire.info.data.repo.InfoRepoImpl
import com.weberbox.pifire.landing.data.repo.LandingRepo
import com.weberbox.pifire.landing.data.repo.LandingRepoImpl
import com.weberbox.pifire.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.pellets.data.repo.PelletsRepoImpl
import com.weberbox.pifire.recipes.data.repo.RecipesRepo
import com.weberbox.pifire.recipes.data.repo.RecipesRepoImpl
import com.weberbox.pifire.settings.data.repo.ManualRepo
import com.weberbox.pifire.settings.data.repo.ManualRepoImpl
import com.weberbox.pifire.settings.data.repo.SettingsRepoImpl
import com.weberbox.pifire.settings.data.repo.SettingsRepo
import com.weberbox.pifire.setup.data.repo.SetupRepo
import com.weberbox.pifire.setup.data.repo.SetupRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindDashRepo(
        dashRepoImpl: DashRepoImpl
    ): DashRepo

    @Binds
    @Singleton
    abstract fun bindPelletsRepo(
        pelletsRepoImpl: PelletsRepoImpl
    ): PelletsRepo

    @Binds
    @Singleton
    abstract fun bindEventsRepo(
        eventsRepoImpl: EventsRepoImpl
    ): EventsRepo

    @Binds
    @Singleton
    abstract fun bindInfoRepo(
        infoRepoImpl: InfoRepoImpl
    ): InfoRepo

    @Binds
    @Singleton
    abstract fun bindSettingsRepo(
        settingsRepoImpl: SettingsRepoImpl
    ): SettingsRepo

    @Binds
    @Singleton
    abstract fun bindManualRepo(
        manualRepoImpl: ManualRepoImpl
    ): ManualRepo

    @Binds
    @Singleton
    abstract fun bindRecipesRepo(
        recipesRepoImpl: RecipesRepoImpl
    ): RecipesRepo

    @Binds
    @Singleton
    abstract fun bindSetupRepo(
        setupRepoImpl: SetupRepoImpl
    ): SetupRepo

    @Binds
    @Singleton
    abstract fun bindLandingRepo(
        landingRepoImpl: LandingRepoImpl
    ): LandingRepo

    @Binds
    @Singleton
    abstract fun bindChangelogRepo(
        changelogRepoImpl: ChangelogRepoImpl
    ): ChangelogRepo

}