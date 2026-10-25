package com.weberbox.pifire.feature.settings.data.di

import com.weberbox.pifire.feature.settings.data.repo.ManualRepo
import com.weberbox.pifire.feature.settings.data.repo.ManualRepoImpl
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
    abstract fun bindManualRepo(
        manualRepoImpl: ManualRepoImpl,
    ): ManualRepo
}
