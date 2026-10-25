package com.weberbox.pifire.feature.dashboard.data.di

import com.weberbox.pifire.feature.dashboard.data.api.DashApi
import com.weberbox.pifire.feature.dashboard.data.api.DashApiImpl
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepo
import com.weberbox.pifire.feature.dashboard.data.repo.DashRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DashDataModule {

    @Binds
    @Singleton
    abstract fun bindDashApi(
        dashApiImpl: DashApiImpl,
    ): DashApi

    @Binds
    @Singleton
    abstract fun bindDashRepo(
        dashRepoImpl: DashRepoImpl,
    ): DashRepo
}
