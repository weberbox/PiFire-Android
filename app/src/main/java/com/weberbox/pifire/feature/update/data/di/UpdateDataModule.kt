package com.weberbox.pifire.feature.update.data.di

import com.weberbox.pifire.feature.update.data.repo.AppUpdateRepo
import com.weberbox.pifire.feature.update.data.repo.AppUpdateRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UpdateDataModule {
    @Binds
    @Singleton
    abstract fun bindAppUpdateRepo(
        appUpdateRepoImpl: AppUpdateRepoImpl,
    ): AppUpdateRepo
}
