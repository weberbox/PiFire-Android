package com.weberbox.pifire.feature.setup.data.di

import com.weberbox.pifire.feature.setup.data.repo.SetupRepo
import com.weberbox.pifire.feature.setup.data.repo.SetupRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SetupDataModule {

    @Binds
    @Singleton
    abstract fun bindSetupRepo(
        setupRepoImpl: SetupRepoImpl,
    ): SetupRepo
}
