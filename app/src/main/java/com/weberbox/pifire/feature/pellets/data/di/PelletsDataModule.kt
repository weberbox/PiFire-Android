package com.weberbox.pifire.feature.pellets.data.di

import com.weberbox.pifire.feature.pellets.data.api.PelletsApi
import com.weberbox.pifire.feature.pellets.data.api.PelletsApiImpl
import com.weberbox.pifire.feature.pellets.data.repo.PelletsRepo
import com.weberbox.pifire.feature.pellets.data.repo.PelletsRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PelletsDataModule {

    @Binds
    @Singleton
    abstract fun bindPelletsApi(
        pelletsApiImpl: PelletsApiImpl,
    ): PelletsApi

    @Binds
    @Singleton
    abstract fun bindPelletsRepo(
        pelletsRepoImpl: PelletsRepoImpl,
    ): PelletsRepo
}
