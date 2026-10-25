package com.weberbox.pifire.feature.info.data.di

import com.weberbox.pifire.feature.info.data.api.InfoApi
import com.weberbox.pifire.feature.info.data.api.InfoApiImpl
import com.weberbox.pifire.feature.info.data.repo.InfoRepo
import com.weberbox.pifire.feature.info.data.repo.InfoRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InfoDataModule {

    @Binds
    @Singleton
    abstract fun bindInfoApi(
        infoApiImpl: InfoApiImpl,
    ): InfoApi

    @Binds
    @Singleton
    abstract fun bindInfoRepo(
        infoRepoImpl: InfoRepoImpl,
    ): InfoRepo
}
