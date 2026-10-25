package com.weberbox.pifire.core.di

import com.weberbox.pifire.core.network.api.SettingsApi
import com.weberbox.pifire.core.network.api.SettingsApiImpl
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.repo.SettingsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Server settings data, used app-wide (several features and core services). */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindSettingsApi(
        settingsApiImpl: SettingsApiImpl,
    ): SettingsApi

    @Binds
    @Singleton
    abstract fun bindSettingsRepo(
        settingsRepoImpl: SettingsRepoImpl,
    ): SettingsRepo
}
