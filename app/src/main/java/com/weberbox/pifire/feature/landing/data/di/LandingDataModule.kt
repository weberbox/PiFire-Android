package com.weberbox.pifire.feature.landing.data.di

import com.weberbox.pifire.feature.landing.data.repo.LandingRepo
import com.weberbox.pifire.feature.landing.data.repo.LandingRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LandingDataModule {

    @Binds
    @Singleton
    abstract fun bindLandingRepo(
        landingRepoImpl: LandingRepoImpl,
    ): LandingRepo
}
