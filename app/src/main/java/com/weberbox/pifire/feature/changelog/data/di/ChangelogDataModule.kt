package com.weberbox.pifire.feature.changelog.data.di

import com.weberbox.pifire.feature.changelog.data.repo.ChangelogRepo
import com.weberbox.pifire.feature.changelog.data.repo.ChangelogRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ChangelogDataModule {

    @Binds
    @Singleton
    abstract fun bindChangelogRepo(
        changelogRepoImpl: ChangelogRepoImpl,
    ): ChangelogRepo
}
