package com.weberbox.pifire.feature.settings.data.di

import com.weberbox.pifire.feature.settings.data.api.SettingsApi
import com.weberbox.pifire.feature.settings.data.api.SettingsApiImpl
import com.weberbox.pifire.feature.settings.data.repo.ManualRepo
import com.weberbox.pifire.feature.settings.data.repo.ManualRepoImpl
import com.weberbox.pifire.feature.settings.data.repo.SettingsRepo
import com.weberbox.pifire.feature.settings.data.repo.SettingsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {

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

    @Binds
    @Singleton
    abstract fun bindManualRepo(
        manualRepoImpl: ManualRepoImpl,
    ): ManualRepo
}
